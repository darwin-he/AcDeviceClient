package DeviceCenter;

import NetService.vo.DeviceInfor;
import NetService.vo.DeviceStateDate;
import NetService.vo.EnviroDate;
import NetService.vo.UserCard;
import device.RfidSensorModule.Mfrc522.StatusCode;
import device.RfidSensorModule.Mfrc522.UID;
import device.StepperMotor.TurnBack;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/10 22:00
 */
public interface ACCDeviceCenter{

	/**
	 * 开门
	 * @param onBack
	 */
	void openDoor(TurnBack onBack);
	
	/**
	 * 关门
	 * @param onBack
	 */
	void closeDoor(TurnBack onBack);

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
	 * 防冲撞及选卡
	 * @return
	 */
	UID anticollAndSelect() throws IOException;
	
	/**
	 * 验证卡片的块密码
	 * @param authKeyA 是否是KeyA
	 * @param blockAddr
	 * @param key
	 * @param uid
	 * @return
	 */
	StatusCode authenticate(boolean authKeyA, byte blockAddr, byte[] key, UID uid) throws IOException;
	
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
	
	/**
	 * 唤醒卡片
	 * @return
	 */
	StatusCode wakeupCard() throws IOException;
	
	/**
	 * 命令卡片进入休眠状态
	 * @return
	 */
	StatusCode haltCard();
	
	/**
	 * 退出验证状态
	 */
	void stopCrypto1();

	/**
	 * 复位设备
	 */
	boolean resetDevice();
	
	DeviceInfor getDeviceInfor();
	
	boolean saveDeviceInfor(DeviceInfor deviceInfor);
	
	DeviceStateDate getDeviceStateDate();
	
	EnviroDate getEnviroDate();
	
}
