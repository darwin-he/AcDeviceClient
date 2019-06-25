package DeviceCenter;

import WebSocket.vo.DeviceInfor;
import WebSocket.vo.DeviceStateDate;
import WebSocket.vo.EnviroDate;
import WebSocket.vo.UserCard;
import device.RfidSensorModule.Mfrc522.StatusCode;
import device.StepperMotor.TurnBack;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/10 22:00
 */
public interface ACCDeviceCenter{

	/**
	 * 开门
	 */
	void openDoor(TurnBack back);
	
	//监听非命令控制的开关门
	void setAutoOpenOrCloseListener(TurnBack autoOpen,TurnBack autoClose);
	
	/**
	 * 关门
	 */
	void closeDoor(TurnBack back);

	/**
	 * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
	 * @return
	 */
	UserCard searchCardOneTime();
	
	/**
	 * 开启自动寻卡
	 * @param autoSearchBack  寻卡成功时的回调函数
	 */
	void startAutoSearchCard(AutoSearchBack autoSearchBack);
	
	/**
	 * 停止自动寻卡
	 * @return
	 */
	boolean stopAutoSearchCard();

	/**
	 * 寻卡
	 * @return
	 */
	StatusCode searchCard() throws IOException;
	
	/**
	 * 写入块数据
	 * @param blockAddr
	 * @param buffer 必须是16字节数据
	 * @return
	 */
	StatusCode writeBlockData(byte blockAddr, byte[] buffer) throws IOException;
	
	/**
	 * 读块数据
	 * @param blockAddr
	 * @return  返回16字节数据
	 */
	byte[] readBlockData(byte blockAddr) throws IOException;
	
	boolean resetDevice();
	
	String getDeviceNumber();
	
	DeviceInfor getDeviceInfor();
	
	DeviceStateDate getDeviceStateDate();
	
	EnviroDate getEnviroDate();
	
}
