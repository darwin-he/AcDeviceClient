package WebSocket.Local;

import DeviceCenter.ACCDeviceCenter;
import WebSocket.msgdefin.CodeEnum;
import WebSocket.msgdefin.MsgResult;
import WebSocket.msgdefin.MsgRoute;
import WebSocket.vo.DeviceInfor;
import WebSocket.vo.DeviceStateDate;
import WebSocket.vo.EnviroDate;
import WebSocket.vo.UserCard;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.LogUtil;

import java.net.URI;

/**
 * @author darwin_he
 * @date 2019/5/9 15:58
 */
public class LocalClientImpl extends WebSocketClient implements LocalClient {
	//消息路由
	private MsgRoute toAdmin;
	private MsgRoute toService;
	//硬件控制中心
	private ACCDeviceCenter deviceCenter;
	
	//环境数据采集线程
	private boolean isContinueCollectEnviroData=false;
	private Thread collectThread=null;
	
	public LocalClientImpl(URI serverUri){
		super(serverUri);
	}

	/**
	 * 连接建立时的回调函数
	 * @param serverHandshake
	 */
	public void onOpen(ServerHandshake serverHandshake) {
		//主动进行身份验证
		try {
			Thread.sleep(1000);
			MsgResult sendData=new MsgResult(toService, CodeEnum.UPDATE_IDENTITY_INFOR.getCode(), CodeEnum.UPDATE_IDENTITY_INFOR.getMsg());
			sendMessage(sendData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 收到消息时的回调函数
	 * @param s
	 */
	public void onMessage(String s) {
		MsgResult acceptData=JSON.parseObject(s).toJavaObject(MsgResult.class);
		MsgRoute backMsgRoute=new MsgRoute();
		backMsgRoute.setFrom(acceptData.getMsgRoute().getTo());
		backMsgRoute.setTo(acceptData.getMsgRoute().getFrom());
		
		int code=acceptData.getCode();
		//用户识别成功
		if (code== CodeEnum.GET_USERINFOR_SUCCEDSS.getCode()){
			deviceCenter.openDoor(isSuccessed -> {
				MsgResult sendData;
				if (isSuccessed){
					sendData=new MsgResult(toAdmin,CodeEnum.OPENDOOR_SUCCESS.getCode(),CodeEnum.OPENDOOR_SUCCESS.getMsg());
				}else {
					sendData=new MsgResult(toAdmin,CodeEnum.OPENDOOR_DEFAULT.getCode(),CodeEnum.OPENDOOR_DEFAULT.getMsg());
				}
				sendMessage(sendData);
			});
			return;
		}
		//读卡片指令
		if (code== CodeEnum.UPDATE_CARD.getCode()){
			//寻找一次
			UserCard userCard=deviceCenter.searchCardOneTime();
			sendUserCardToAdmin(userCard);
			deviceCenter.startAutoSearchCard(userCard1 -> {
				getUserByUserCard(userCard);
			});
			return;
		}
		//开门
		if (code== CodeEnum.OPENDOOR.getCode()){
			deviceCenter.openDoor(isSuccessed -> {
				MsgResult sendData;
				if(isSuccessed){
					sendData=new MsgResult(toAdmin, CodeEnum.OPENDOOR_SUCCESS.getCode(), CodeEnum.OPENDOOR_SUCCESS.getMsg());
				}else {
					sendData=new MsgResult(toAdmin, CodeEnum.OPENDOOR_DEFAULT.getCode(), CodeEnum.OPENDOOR_DEFAULT.getMsg());
				}
				sendMessage(sendData);
			});
			return;
		}
		//关门
		if (code== CodeEnum.CLOSEDOOR.getCode()){
			deviceCenter.closeDoor(isSuccessed -> {
				MsgResult sendData;
				if (isSuccessed){
					sendData=new MsgResult(toAdmin, CodeEnum.CCLOSEDOOR_SUCCESS.getCode(), CodeEnum.CCLOSEDOOR_SUCCESS.getMsg());
				}else {
					sendData=new MsgResult(toAdmin, CodeEnum.CLOSEDOOR_DEFAULT.getCode(), CodeEnum.CLOSEDOOR_DEFAULT.getMsg());
				}
				sendMessage(sendData);
			});
			return;
		}
		//与服务器连接成功并进行了身份验证
		if (code==CodeEnum.UPDATE_IDENTITY_INFOR_SUCCESS.getCode()){
			deviceCenter.setAutoOpenOrCloseListener(isSuccessed -> {
				MsgResult sendData;
				if(isSuccessed){
					sendData=new MsgResult(toAdmin, CodeEnum.OPENDOOR_SUCCESS.getCode(), CodeEnum.OPENDOOR_SUCCESS.getMsg());
				}else {
					sendData=new MsgResult(toAdmin, CodeEnum.OPENDOOR_DEFAULT.getCode(), CodeEnum.OPENDOOR_DEFAULT.getMsg());
				}
				sendMessage(sendData);
			}, isSuccessed -> {
				MsgResult sendData;
				if(isSuccessed){
					sendData=new MsgResult(toAdmin, CodeEnum.CCLOSEDOOR_SUCCESS.getCode(), CodeEnum.CCLOSEDOOR_SUCCESS.getMsg());
				}else {
					sendData=new MsgResult(toAdmin, CodeEnum.CLOSEDOOR_DEFAULT.getCode(), CodeEnum.CLOSEDOOR_DEFAULT.getMsg());
				}
				sendMessage(sendData);
			});
			deviceCenter.startAutoSearchCard(userCard -> {
				getUserByUserCard(userCard);
			});
			startCollectEnvirData();
			return;
		}
		//复位设备
		if (code== CodeEnum.RESET_DEVICE.getCode()){
			stopCollectEnvirData();
			MsgResult sendData;
			if (deviceCenter.resetDevice()){
				deviceCenter.startAutoSearchCard(userCard -> { getUserByUserCard(userCard);});
				sendData=new MsgResult(toAdmin, CodeEnum.RESET_DEVICE_SUCCESS.getCode(), CodeEnum.RESET_DEVICE_SUCCESS.getMsg());
			}else {
				sendData=new MsgResult(toAdmin, CodeEnum.RESET_DEVICE_DEFAULT.getCode(), CodeEnum.RESET_DEVICE_DEFAULT.getMsg());
			}
			sendMessage(sendData);
			startCollectEnvirData();
			return;
		}
		//清理日志
		if (code== CodeEnum.CLEAN_LOG.getCode()){
			cleanMemory();
		}else if (code== CodeEnum.UPDATA_DOOR_STATE.getCode()){//上传门禁状态信息
			sendStateDate(backMsgRoute);
		}else if (code== CodeEnum.UPDATE_ENVIRODATE.getCode()){//上传环境数据
			sendEnvironmentDate(backMsgRoute);
		}else if (code== CodeEnum.UPDATE_DEVICE_INFOR.getCode()){//上传设备信息
			sendDeviceInfor(backMsgRoute);
		}
	}

	/**
	 * 连接断开时的回调函数
	 * @param i code
	 * @param s 关闭原因
	 * @param b 远程
	 */
	public void onClose(int i, String s, boolean b) {
		stopCollectEnvirData();
		deviceCenter.stopAutoSearchCard();
		deviceCenter.setAutoOpenOrCloseListener(null,null);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (isClosed()){
//					connect();
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
	}

	/**
	 * 发生错误时的回调函数
	 * @param e
	 */
	public void onError(Exception e) { }
	
	//////////////////////////////////////////实现来自MyNetService的接口/////////////////////////////////////////////////
	/**
	 * 通过卡片号获取用户信息
	 * @param userCard
	 * @return
	 */
	private void getUserByUserCard(UserCard userCard) {
		MsgResult sendData=new MsgResult(toService, CodeEnum.GET_USERINFOR.getCode(), CodeEnum.GET_USERINFOR.getMsg(),userCard);
		sendMessage(sendData);
	}
	
	/**
	 * 连接到服务器
	 * @return
	 */
	@Override
	public void connectToService(ACCDeviceCenter deviceCenter) throws Exception {
		if (deviceCenter==null){
			throw new Exception("未传入ACCDeviceCenter引用！");
		}
		this.deviceCenter=deviceCenter;
		String deviceNumber=deviceCenter.getDeviceNumber();
		toAdmin=new MsgRoute();
		toAdmin.setFrom("D"+deviceNumber);
		toAdmin.setTo("A"+deviceNumber);
		
		toService=new MsgRoute();
		toService.setTo("LocalService");
		toService.setFrom("D"+deviceNumber);
		
		connect();
	}
	
	/**
	 * 与服务器断开连接
	 *
	 * @return
	 */
	public void disConnect() {
		close();
	}
	//////////////////////////////////////////实现来自MyNetService的接口结束/////////////////////////////////////////////////

	/**
	 * 清理内存
	 */
	private void cleanMemory(){
		MsgResult sendData;
		if (LogUtil.cleanLogData()){
			sendData=new MsgResult(toAdmin, CodeEnum.CLEAN_LOG_SUCCESS.getCode(), CodeEnum.CLEAN_LOG_SUCCESS.getMsg());
		}else {
			sendData=new MsgResult(toAdmin, CodeEnum.CLEAN_LOG_DEFAULT.getCode(), CodeEnum.CLEAN_LOG_DEFAULT.getMsg());
		}
		sendMessage(sendData);
	}

	/**
	 * 发送卡片号给管理，此函数一般在管理员添加新用户时
	 * @param userCard
	 * @return
	 */
	private void sendUserCardToAdmin(UserCard userCard) {
		MsgResult sendData;
		if (userCard!=null){
			sendData=new MsgResult(toAdmin, CodeEnum.UPDATE_CARD_SUCCESS.getCode(), CodeEnum.UPDATE_CARD_SUCCESS.getMsg(),userCard);
		}else {
			sendData=new MsgResult(toAdmin, CodeEnum.UPDATE_CARD_DEFAULT.getCode(), CodeEnum.UPDATE_CARD_DEFAULT.getMsg());
		}
		sendMessage(sendData);
	}
	
	/**
	 * 上传环境数据
	 *
	 * @return
	 */
	private void sendEnvironmentDate(MsgRoute msgRoute) {
		EnviroDate enviroDate=deviceCenter.getEnviroDate();
		MsgResult sendData;
		if (enviroDate!=null){
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATA_ENVIRODATE_SUCCESS.getCode(), CodeEnum.UPDATA_ENVIRODATE_SUCCESS.getMsg(),enviroDate);
		}else {
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATE_ENVIRODATE_DEFAULT.getCode(), CodeEnum.UPDATE_ENVIRODATE_DEFAULT.getMsg());
		}
		sendMessage(sendData);
	}

	/**
	 * 自动采集环境数据并上传，30分钟采集一次。
	 */
	private void startCollectEnvirData(){
		if (isContinueCollectEnviroData)
			return;
		isContinueCollectEnviroData=true;
		
		if (collectThread!=null&&collectThread.isAlive())
			return;
		collectThread=new Thread(() -> {
			while (isContinueCollectEnviroData){
				try {
					Thread.sleep(1800000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					isContinueCollectEnviroData=false;
				}
				sendEnvironmentDate(toService);
				sendEnvironmentDate(toAdmin);
			}
		});
		collectThread.start();
	}

	/**
	 * 停止采集环境数据
	 */
	private void stopCollectEnvirData(){
		isContinueCollectEnviroData=false;
	}
	
	/**
	 * 上传门禁状态信息
	 * @return
	 */
	private void sendStateDate(MsgRoute msgRoute) {
		DeviceStateDate stateDate=deviceCenter.getDeviceStateDate();
		MsgResult sendData;
		if (stateDate!=null){
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATE_DOOR_STATE_SUCCESS.getCode(), CodeEnum.UPDATE_DOOR_STATE_SUCCESS.getMsg(),stateDate);
		}else {
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATE_DOOR_STATE_DEFAULT.getCode(), CodeEnum.UPDATE_DOOR_STATE_DEFAULT.getMsg());
		}
		sendMessage(sendData);
	}
	
	/**
	 * 上传设备信息
	 */
	private void sendDeviceInfor(MsgRoute msgRoute) {
		DeviceInfor deviceInfor=deviceCenter.getDeviceInfor();
		MsgResult sendData;
		if (deviceInfor!=null){
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATE_DEVICE_INFOR_SUCCESS.getCode(), CodeEnum.UPDATE_DEVICE_INFOR_SUCCESS.getMsg(),deviceInfor);
		}else {
			sendData=new MsgResult(msgRoute, CodeEnum.UPDATE_DEVICE_INFOR_DEFAULT.getCode(), CodeEnum.UPDATE_DEVICE_INFOR_DEFAULT.getMsg());
		}
		sendMessage(sendData);
	}
	
	private void sendMessage(MsgResult msgResult){
		String sendString=JSON.toJSONString(msgResult);
		send(sendString);
	}
	
}
