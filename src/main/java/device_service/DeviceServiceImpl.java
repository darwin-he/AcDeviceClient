package device_service;

import control_center.ControlCenter;
import device_service.msg.Code;
import device_service.msg.Message;
import device_service.msg.Route;
import device_service.dto.DeviceInfo;
import device_service.dto.DeviceState;
import device_service.dto.EnviroInfo;
import device_service.dto.UserCard;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.LogUtil;

import java.net.URI;

/**
 * @author darwin_he
 * @date 2019/5/9 15:58
 */
public class DeviceServiceImpl extends WebSocketClient implements DeviceService {
	//消息路由
	private Route toAdmin;
	private Route toService;

	//硬件控制中心
	private ControlCenter controlCenter;
	
	//环境数据采集线程
	private boolean collectEnviroData =false;
	private Thread collectThread=null;
	
	public DeviceServiceImpl(URI serverUri){
		super(serverUri);
	}

	/**
	 * WebSocket连接建立时的回调函数
	 * @param serverHandshake
	 */
	public void onOpen(ServerHandshake serverHandshake) {
		//主动进行身份验证
		try {
			Thread.sleep(1000);
			Message sendData=new Message(toService, Code.UPDATE_IDENTITY_INFOR, null);
			sendMessage(sendData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Route overturnRoute(Route route) {
		Route turnedRoute =new Route();
		turnedRoute.setFrom(route.getTo());
		turnedRoute.setTo(route.getFrom());
		return turnedRoute;
	}

	/**
	 * WebSocket收到消息时的回调函数
	 * @param msg
	 */
	public void onMessage(String msg) {
		Message message=JSON.parseObject(msg).toJavaObject(Message.class);
		Route backRoute = overturnRoute(message.getRoute());
		
		int code=message.getCode();
		//用户识别成功
		if (code== Code.GET_USERINFOR_SUCCEDSS.getCode()){
			controlCenter.openDoor(isSucceed -> {
				Message sendData;
				if (isSucceed){
					sendData=new Message(toAdmin, Code.OPENDOOR_SUCCESS);
				}else {
					sendData=new Message(toAdmin, Code.OPENDOOR_DEFAULT);
				}
				sendMessage(sendData);
			});
			return;
		}
		//读卡片指令
		if (code== Code.UPDATE_CARD.getCode()){
			UserCard userCard = controlCenter.searchCardOnce();
			sendUserCardToAdmin(userCard);
			controlCenter.startAutoSearchCard(this::getUserByUserCard);
			return;
		}
		//开门
		if (code== Code.OPENDOOR.getCode()){
			controlCenter.openDoor(isSucceed -> {
				Message sendData;
				if(isSucceed){
					sendData=new Message(toAdmin, Code.OPENDOOR_SUCCESS);
				}else {
					sendData=new Message(toAdmin, Code.OPENDOOR_DEFAULT);
				}
				sendMessage(sendData);
			});
			return;
		}
		//关门
		if (code== Code.CLOSEDOOR.getCode()){
			controlCenter.closeDoor(isSucceed -> {
				Message sendData;
				if (isSucceed){
					sendData=new Message(toAdmin, Code.CCLOSEDOOR_SUCCESS);
				}else {
					sendData=new Message(toAdmin, Code.CLOSEDOOR_DEFAULT);
				}
				sendMessage(sendData);
			});
			return;
		}
		//与服务器连接成功并进行了身份验证
		if (code== Code.UPDATE_IDENTITY_INFOR_SUCCESS.getCode()){
			controlCenter.setDoorListener(isSucceed -> {
				Message sendData;
				if(isSucceed){
					sendData=new Message(toAdmin, Code.OPENDOOR_SUCCESS);
				}else {
					sendData=new Message(toAdmin, Code.OPENDOOR_DEFAULT);
				}
				sendMessage(sendData);
			}, isSucceed -> {
				Message sendData;
				if(isSucceed){
					sendData=new Message(toAdmin, Code.CCLOSEDOOR_SUCCESS);
				}else {
					sendData=new Message(toAdmin, Code.CLOSEDOOR_DEFAULT);
				}
				sendMessage(sendData);
			});
			controlCenter.startAutoSearchCard(this::getUserByUserCard);
			startCollectEnviroData();
			return;
		}
		//复位设备
		if (code== Code.RESET_DEVICE.getCode()){
			stopCollectEnviroData();
			Message sendData;
			if (controlCenter.reset()){
				controlCenter.startAutoSearchCard(this::getUserByUserCard);
				sendData=new Message(toAdmin, Code.RESET_DEVICE_SUCCESS);
			}else {
				sendData=new Message(toAdmin, Code.RESET_DEVICE_DEFAULT);
			}
			sendMessage(sendData);
			startCollectEnviroData();
			return;
		}
		//清理日志
		if (code== Code.CLEAN_LOG.getCode()){
			cleanMemory();
		}else if (code== Code.UPDATA_DOOR_STATE.getCode()){//上传门禁状态信息
			uploadDeviceState(backRoute);
		}else if (code== Code.UPDATE_ENVIRODATE.getCode()){//上传环境数据
			uploadEnvironmentDate(backRoute);
		}else if (code== Code.UPDATE_DEVICE_INFOR.getCode()){//上传设备信息
			uploadDeviceInfo(backRoute);
		}
	}

	/**
	 * WebSocket连接断开时的回调函数
	 * @param i code
	 * @param s 关闭原因
	 * @param b 远程
	 */
	public void onClose(int i, String s, boolean b) {
		stopCollectEnviroData();
		controlCenter.stopAutoSearchCard();
		controlCenter.setDoorListener(null,null);
		new Thread(() -> {
			while (isClosed()){
				reconnect();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * WebSocket发生错误时的回调函数
	 * @param e
	 */
	public void onError(Exception e) {

	}
	
	//////////////////////////////////////////实现来自MyNetService的接口/////////////////////////////////////////////////
	/**
	 * 通过卡片号获取用户信息
	 * @param userCard
	 * @return
	 */
	private void getUserByUserCard(UserCard userCard) {
		Message sendData=new Message(toService, Code.GET_USERINFOR, userCard);
		sendMessage(sendData);
	}
	
	/**
	 * 连接到服务器
	 * @return
	 */
	@Override
	public void start(ControlCenter deviceCenter) {
		this.controlCenter =deviceCenter;
		String deviceNumber=deviceCenter.getDeviceNumber();
		toAdmin=new Route();
		toAdmin.setFrom("D"+deviceNumber);
		toAdmin.setTo("A"+deviceNumber);
		
		toService=new Route();
		toService.setTo("LocalService");
		toService.setFrom("D"+deviceNumber);
		
		connect();
	}

	@Override
	public void restart(ControlCenter controlCenter) {

	}

	/**
	 * 与服务器断开连接
	 *
	 * @return
	 */
	public void shutdown() {
		close();
	}
	//////////////////////////////////////////实现来自MyNetService的接口结束/////////////////////////////////////////////////

	/**
	 * 清理内存
	 */
	private void cleanMemory(){
		Message sendData;
		if (LogUtil.cleanLogData()){
			sendData=new Message(toAdmin, Code.CLEAN_LOG_SUCCESS);
		}else {
			sendData=new Message(toAdmin, Code.CLEAN_LOG_DEFAULT);
		}
		sendMessage(sendData);
	}

	/**
	 * 发送卡片号给管理，此函数一般在管理员添加新用户时
	 * @param userCard
	 * @return
	 */
	private void sendUserCardToAdmin(UserCard userCard) {
		Message sendData;
		if (userCard !=null){
			sendData=new Message(toAdmin, Code.UPDATE_CARD_SUCCESS, userCard);
		}else {
			sendData=new Message(toAdmin, Code.UPDATE_CARD_DEFAULT);
		}
		sendMessage(sendData);
	}
	
	/**
	 * 上传环境数据
	 *
	 * @return
	 */
	private void uploadEnvironmentDate(Route route) {
		EnviroInfo enviroInfo = controlCenter.getEnviroDate();
		Message sendData;
		if (enviroInfo !=null){
			sendData=new Message(route, Code.UPDATA_ENVIRODATE_SUCCESS, enviroInfo);
		}else {
			sendData=new Message(route, Code.UPDATE_ENVIRODATE_DEFAULT);
		}
		sendMessage(sendData);
	}

	/**
	 * 自动采集环境数据并上传，30分钟采集一次。
	 */
	private void startCollectEnviroData(){
		if (collectEnviroData)
			return;
		collectEnviroData =true;
		
		if (collectThread!=null&&collectThread.isAlive())
			return;
		collectThread=new Thread(() -> {
			while (collectEnviroData){
				try {
					Thread.sleep(1800000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					collectEnviroData =false;
				}
				uploadEnvironmentDate(toService);
				uploadEnvironmentDate(toAdmin);
			}
		});
		collectThread.start();
	}

	/**
	 * 停止采集环境数据
	 */
	private void stopCollectEnviroData(){
		collectEnviroData =false;
	}
	
	/**
	 * 上传门禁状态信息
	 * @return
	 */
	private void uploadDeviceState(Route route) {
		DeviceState stateDate= controlCenter.getDeviceStateDate();
		Message sendData;
		if (stateDate!=null){
			sendData=new Message(route, Code.UPDATE_DOOR_STATE_SUCCESS,stateDate);
		}else {
			sendData=new Message(route, Code.UPDATE_DOOR_STATE_DEFAULT);
		}
		sendMessage(sendData);
	}
	
	/**
	 * 上传设备信息
	 */
	private void uploadDeviceInfo(Route route) {
		DeviceInfo deviceInfo = controlCenter.getDeviceInfor();
		Message sendData;
		if (deviceInfo !=null){
			sendData=new Message(route, Code.UPDATE_DEVICE_INFOR_SUCCESS, deviceInfo);
		}else {
			sendData=new Message(route, Code.UPDATE_DEVICE_INFOR_DEFAULT);
		}
		sendMessage(sendData);
	}

	/**
	 * 发送消息给服务器
	 * @param message
	 */
	private void sendMessage(Message message){
		String sendString=JSON.toJSONString(message);
		send(sendString);
	}
	
}
