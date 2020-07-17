package control_center;

import device_service.dto.DeviceInfo;
import device_service.dto.DeviceState;
import device_service.dto.EnviroInfo;
import device_service.dto.UserCard;
import components.rfid_reader.mfrc522.StatusCode;
import components.stepper_motor.OnTurned;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/10 22:00
 */
public interface ControlCenter {

	/**
	 * 开门
	 */
	void openDoor(OnTurned back);

	/**
	 * 关门
	 */
	void closeDoor(OnTurned back);
	
	//监听非命令控制的开关门
	void setDoorListener(OnTurned autoOpen, OnTurned autoClose);

	/**
	 * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
	 * @return
	 */
	UserCard searchCardOnce();
	
	/**
	 * 开启自动寻卡
	 * @param autoSearchBack  寻卡成功时的回调函数
	 */
	void startAutoSearchCard(AutoSearchBack autoSearchBack);
	
	/**
	 * 停止自动寻卡
	 * @return
	 */
	void stopAutoSearchCard();
	
	/**
	 * 写入块数据
	 * @param blockAddr
	 * @param buffer 必须是16字节数据
	 * @return
	 */
	StatusCode writeBlockDataToCard(byte blockAddr, byte[] buffer) throws IOException;
	
	/**
	 * 读块数据
	 * @param blockAddr
	 * @return  返回16字节数据
	 */
	byte[] readBlockDataFromCard(byte blockAddr) throws IOException;
	
	boolean reset();
	
	String getDeviceNumber();
	
	DeviceInfo getDeviceInfor();
	
	DeviceState getDeviceStateDate();
	
	EnviroInfo getEnviroDate();
	
}
