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
	void openDoor(OnTurned onOpened,OnTurned onClosed);

	/**
	 * 关门
	 */
	void closeDoor(OnTurned back);

	/**
	 * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
	 * @return
	 */
	UserCard searchCardOnce();

	void enableHumanHandle(OnTurned onOpened, OnTurned onClosed);

	void forbidHumanHandle();

	/**
	 * 开启自动寻卡
	 * @param outsideRfidSearchBack 寻到卡片后的回调函数
	 * @param insideRfidSearchBack 寻到卡片后的回调函数
	 */
	void startAutoSearchCard(AutoSearchBack outsideRfidSearchBack, AutoSearchBack insideRfidSearchBack);
	
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
	
	void reset();
	
	String getDeviceNumber();
	
	DeviceInfo getDeviceInfor();
	
	DeviceState getDeviceStateDate();
	
	EnviroInfo getEnviroDate();
	
}
