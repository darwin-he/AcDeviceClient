package NetService;

import DeviceCenter.ACCDeviceCenter;
import NetService.vo.DeviceInfor;
import NetService.vo.DeviceStateDate;
import NetService.vo.EnviroDate;
import NetService.vo.UserCard;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.CommonResult;
import utils.DeviceCodeEnum;

import java.net.URI;

/**
 * @author darwin_he
 * @date 2019/5/9 15:58
 */
public class MyNetServiceImpl extends WebSocketClient implements MyNetService {
	
	private ACCDeviceCenter deviceCenter;

	public void setDeviceCenter(ACCDeviceCenter deviceCenter) {
		this.deviceCenter = deviceCenter;
	}
	
	public MyNetServiceImpl(URI serverUri,ACCDeviceCenter deviceCenter){
		this(serverUri);
		this.deviceCenter=deviceCenter;
	}

	public MyNetServiceImpl(URI serverUri){
		super(serverUri);
	}

	/**
	 * 连接建立时的回调函数
	 * @param serverHandshake
	 */
	public void onOpen(ServerHandshake serverHandshake) {
		//开启自动寻卡功能
		deviceCenter.startAutoSearchCard(userCard -> {
			//识别用户身份
			getUserByUserCard(userCard);
		});
	}

	/**
	 * 收到消息时的回调函数
	 * @param s
	 */
	public void onMessage(String s) {
		CommonResult acceptData=JSON.parseObject(s).toJavaObject(CommonResult.class);
		int code=acceptData.getCode();
		if (code==DeviceCodeEnum.GET_USERINFOR_SUCCEDSS.getCode()){//用户识别成功
			//命令卡片休眠
			deviceCenter.haltCard();
			deviceCenter.openDoor(null);
		}else if (code==DeviceCodeEnum.UPDATE_CARD.getCode()){//读卡片指令
			//寻找一张卡
			UserCard userCard=deviceCenter.searchCardOneTime();
			sendUserCardToService(userCard);
			if (userCard!=null){
				//命令卡片休眠
				deviceCenter.haltCard();
			}
			//重新开启自动寻卡功能
			deviceCenter.startAutoSearchCard(userCard1 -> {
				//识别用户身份
				getUserByUserCard(userCard);
			});
		}else if (code==DeviceCodeEnum.OPENDOOR.getCode()){//开门
			deviceCenter.openDoor(isSuccessed -> {
				if(isSuccessed){
					CommonResult sendData=new CommonResult(DeviceCodeEnum.OPENDOOR_SUCCESS.getCode(),DeviceCodeEnum.OPENDOOR_SUCCESS.getMsg());
					String sendString=JSON.toJSONString(sendData);
					send(sendString);
				}else {
					CommonResult sendData=new CommonResult(DeviceCodeEnum.OPENDOOR_DEFAULT.getCode(),DeviceCodeEnum.OPENDOOR_DEFAULT.getMsg());
					String sendString=JSON.toJSONString(sendData);
					send(sendString);
				}
			});
		}else if (code==DeviceCodeEnum.CLOSEDOOR.getCode()){//关门
			deviceCenter.closeDoor(isSuccessed -> {
				if (isSuccessed){
					CommonResult sendData=new CommonResult(DeviceCodeEnum.CCLOSEDOOR_SUCCESS.getCode(),DeviceCodeEnum.CCLOSEDOOR_SUCCESS.getMsg());
					String sendString=JSON.toJSONString(sendData);
					send(sendString);
				}else {
					CommonResult sendData=new CommonResult(DeviceCodeEnum.CLOSEDOOR_DEFAULT.getCode(),DeviceCodeEnum.CLOSEDOOR_DEFAULT.getMsg());
					String sendString=JSON.toJSONString(sendData);
					send(sendString);
				}
			});
		}else if (code==DeviceCodeEnum.RESET_DEVICE.getCode()){//复位设备
			if (deviceCenter.resetDevice()){
				//开启自动寻卡功能
				deviceCenter.startAutoSearchCard(userCard -> {
					//识别用户身份
					getUserByUserCard(userCard);
				});
				CommonResult sendData=new CommonResult(DeviceCodeEnum.RESET_DEVICE_SUCCESS.getCode(),DeviceCodeEnum.RESET_DEVICE_SUCCESS.getMsg());
				String sendString=JSON.toJSONString(sendData);
				send(sendString);
			}else {
				CommonResult sendData=new CommonResult(DeviceCodeEnum.RESET_DEVICE_DEFAULT.getCode(),DeviceCodeEnum.RESET_DEVICE_DEFAULT.getMsg());
				String sendString=JSON.toJSONString(sendData);
				send(sendString);
			}
		}else if (code==DeviceCodeEnum.CLEAN_LOG.getCode()){//清理日志
		
		}else if (code==DeviceCodeEnum.UPDATA_DOOR_STATE.getCode()){//上传门禁状态信息
			sendStateDateToService();
		}else if (code==DeviceCodeEnum.UPDATE_ENVIRODATE.getCode()){//上传环境数据
			sendEnvironmentDateToService();
		}else if (code==DeviceCodeEnum.UPDATE_DEVICE_INFOR.getCode()){//上传设备信息
			sendDeviceInforToService();
		}
		
	}

	/**
	 * 连接断开时的回调函数
	 * @param i code
	 * @param s 关闭原因
	 * @param b 远程
	 */
	public void onClose(int i, String s, boolean b) {
		deviceCenter.stopAutoSearchCard();
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
	public void getUserByUserCard(UserCard userCard) {
		CommonResult sendData=new CommonResult(DeviceCodeEnum.GET_USERINFOR.getCode(),DeviceCodeEnum.GET_USERINFOR.getMsg(),userCard);
		String sendString=JSON.toJSONString(sendData);
		send(sendString);
	}
	
	/**
	 * 连接到服务器
	 *
	 * @return
	 */
	public void connectToService() throws Exception {
		if (deviceCenter==null){
			throw new Exception("未传入ACCDeviceCenter引用！");
		}
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
	
	/**
	 * 发送卡片号给服务器，此函数一般在管理员添加新用户时
	 * 客服端通过此函数将未注册的卡片号返回给服务器。
	 * @param userCard
	 * @return
	 */
	@Override
	public void sendUserCardToService(UserCard userCard) {
		CommonResult sendData;
		if (userCard!=null){
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_CARD_SUCCESS.getCode(),DeviceCodeEnum.UPDATE_CARD_SUCCESS.getMsg(),userCard);
		}else {
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_CARD_DEFAULT.getCode(),DeviceCodeEnum.UPDATE_CARD_DEFAULT.getMsg());
		}
		String sendString=JSON.toJSONString(sendData);
		send(sendString);
	}
	
	/**
	 * 发送环境数据给服务器
	 *
	 * @return
	 */
	@Override
	public void sendEnvironmentDateToService( ) {
		EnviroDate enviroDate=deviceCenter.getEnviroDate();
		CommonResult sendData;
		if (enviroDate!=null){
			sendData=new CommonResult(DeviceCodeEnum.UPDATA_ENVIRODATE_SUCCESS.getCode(),DeviceCodeEnum.UPDATA_ENVIRODATE_SUCCESS.getMsg(),enviroDate);
		}else {
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_ENVIRODATE_DEFAULT.getCode(),DeviceCodeEnum.UPDATE_ENVIRODATE_DEFAULT.getMsg());
		}
		String sendString=JSON.toJSONString(sendData);
		send(sendString);
	}
	
	/**
	 * 发送门禁状态数据给服务器
	 * @return
	 */
	@Override
	public void sendStateDateToService( ) {
		DeviceStateDate stateDate=deviceCenter.getDeviceStateDate();
		CommonResult sendData;
		if (stateDate!=null){
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_DOOR_STATE_SUCCESS.getCode(),DeviceCodeEnum.UPDATE_DOOR_STATE_SUCCESS.getMsg(),stateDate);
		}else {
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_DOOR_STATE_DEFAULT.getCode(),DeviceCodeEnum.UPDATE_DOOR_STATE_DEFAULT.getMsg());
		}
		String sendString=JSON.toJSONString(sendData);
		send(sendString);
	}

	/**
	 * 发送设备信息给服务器
	 *
	 */
	@Override
	public void sendDeviceInforToService() {
		DeviceInfor deviceInfor=deviceCenter.getDeviceInfor();
		CommonResult sendData;
		if (deviceInfor!=null){
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_DEVICE_INFOR_SUCCESS.getCode(),DeviceCodeEnum.UPDATE_DEVICE_INFOR_SUCCESS.getMsg(),deviceInfor);
		}else {
			sendData=new CommonResult(DeviceCodeEnum.UPDATE_DEVICE_INFOR_DEFAULT.getCode(),DeviceCodeEnum.UPDATE_DEVICE_INFOR_DEFAULT.getMsg());
		}
		String sendString=JSON.toJSONString(sendData);
		send(sendString);
	}
	
}
