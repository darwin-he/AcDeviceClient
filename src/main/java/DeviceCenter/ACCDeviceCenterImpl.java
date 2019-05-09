package DeviceCenter;

import NetService.vo.DeviceInfor;
import NetService.vo.DeviceStateDate;
import NetService.vo.EnviroDate;
import NetService.vo.UserCard;
import com.pi4j.io.gpio.RaspiPin;
import device.GraSensorModule.GraSensorModuleImpl;
import device.GraSensorModule.GraSensorModulePin;
import device.RfidSensorModule.Mfrc522.StatusCode;
import device.RfidSensorModule.Mfrc522.UID;
import device.RfidSensorModule.MyRfidImpl;
import device.RfidSensorModule.RfidPin;
import device.StepperMotor.StepperMotorImpl;
import device.StepperMotor.StepperMotorPin;
import device.StepperMotor.TurnBack;
import device.TouchSensor.TouchSensorImpl;
import device.TouchSensor.TouchSensorPin;
import utils.Hex;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
public class ACCDeviceCenterImpl implements ACCDeviceCenter {
	
	private GraSensorModuleImpl graSensorModule;
	private MyRfidImpl myRfid;
	private StepperMotorImpl stepperMotor;
	private TouchSensorImpl touchSensor;

	private GraSensorModulePin graSensorModulePin;
	private RfidPin rfidPin;
	private StepperMotorPin stepperMotorPin;
	private TouchSensorPin touchSensorPin;
	
	public ACCDeviceCenterImpl() throws IOException {
		graSensorModulePin=new GraSensorModulePin(RaspiPin.GPIO_25);
		rfidPin=new RfidPin(RaspiPin.GPIO_06);
		stepperMotorPin=new StepperMotorPin(RaspiPin.GPIO_00,RaspiPin.GPIO_02,RaspiPin.GPIO_03,RaspiPin.GPIO_04);
		touchSensorPin=new TouchSensorPin(RaspiPin.GPIO_05);
		
		graSensorModule=new GraSensorModuleImpl(graSensorModulePin);
		myRfid=new MyRfidImpl(rfidPin);
		stepperMotor=new StepperMotorImpl(stepperMotorPin);
		touchSensor=new TouchSensorImpl(touchSensorPin);
		//开启开门检测
		touchSensor.setTouchSensorListener(gpioPinDigitalStateChangeEvent -> {
			//由低电平变为高电平说明正有物品触摸在传感器上
			if (gpioPinDigitalStateChangeEvent.getState().isHigh()){
				//开门(如果需要记录小区出门记录，可以在此处的onpenDoor函数添加回调)
				openDoor(null);
			}
		});
	}
	

	/**
	 * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
	 * @return
	 */
	@Override
	public UserCard searchCardOneTime() {
		UID uid=myRfid.searchCardOneTime();
		UserCard userCard=null;
		if (uid!=null){
			userCard=new UserCard();
			userCard.setUserCard(Hex.encodeHexString(uid.getUidBytes()));
		}
		return userCard;
	}

	/**
	 * 开启自动寻卡
	 * @param searchBack 寻卡成功时的回调函数
	 */
	@Override
	public void startAutoSearchCard(AutoSearchBack searchBack) {
		myRfid.startAutoSearchCard(uid -> {
			UserCard userCard=new UserCard();
			userCard.setUserCard(Hex.encodeHexString(uid.getUidBytes()));
			searchBack.onSearchOnUserCard(userCard);
		});
	}
	
	/**
	 * 停止自动寻卡
	 *
	 * @return
	 */
	@Override
	public boolean stopAutoSearchCard() {
		return myRfid.stopAutoSearchCard();
	}
	
	/**
	 * 开门
	 * @param onBack
	 */
	@Override
	public void openDoor(TurnBack onBack) {
		stepperMotor.openDoor(isSuccessed -> {
			//成功开门后开启通过检测
			startPassCheck();
			//执行回调函数
			if (onBack!=null)
				onBack.turnFinshed(isSuccessed);
		});
	}

	/**
	 * 通过检测
	 */
	private void startPassCheck(){
		graSensorModule.setGraSensorModuleListener(gpioPinDigitalStateChangeEvent -> {
			//无物品则为低电平，这里再次变为低电平说明有物体已经通过
			if (gpioPinDigitalStateChangeEvent.getState().isLow()){
				//关闭通过检测
				graSensorModule.setGraSensorModuleListener(null);
				//关门
				closeDoor(null);
			}
		});
	}
	
	/**
	 * 关门
	 * @param onBack
	 */
	@Override
	public void closeDoor(TurnBack onBack) {
		stepperMotor.closeDoor(isSuccessed -> {
			if (onBack!=null)
				onBack.turnFinshed(isSuccessed);
		});
	}

	/**
	 * 寻卡
	 *
	 * @return
	 */
	@Override
	public StatusCode searchCard() throws IOException {
		return myRfid.searchCard();
	}

	/**
	 * 防冲撞及选卡
	 *
	 * @return
	 */
	@Override
	public UID anticollAndSelect() throws IOException {
		return myRfid.anticollAndSelect();
	}

	/**
	 * 验证卡片的块密码
	 *
	 * @param authKeyA  是否是KeyA
	 * @param blockAddr
	 * @param key
	 * @param uid
	 * @return
	 */
	@Override
	public StatusCode authenticate(boolean authKeyA, byte blockAddr, byte[] key, UID uid) throws IOException {
		return myRfid.authenticate(authKeyA,blockAddr,key,uid);
	}

	/**
	 * 写入块数据
	 *
	 * @param blockAddr
	 * @param buffer    必须是16字节数据
	 * @return
	 */
	@Override
	public StatusCode writeBlockData(byte blockAddr, byte[] buffer) throws IOException {
		return myRfid.writeBlockData(blockAddr,buffer);
	}

	/**
	 * 读块数据
	 *
	 * @param blockAddr
	 * @return 返回16字节数据
	 */
	@Override
	public byte[] readBlockData(byte blockAddr) throws IOException {
		return myRfid.readBlockData(blockAddr);
	}

	/**
	 * 唤醒卡片
	 *
	 * @return
	 */
	@Override
	public StatusCode wakeupCard() throws IOException {
		return myRfid.wakeupCard();
	}

	/**
	 * 命令卡片进入休眠状态
	 *
	 * @return
	 */
	@Override
	public StatusCode haltCard() {
		return myRfid.haltCard();
	}

	/**
	 * 退出验证状态
	 */
	@Override
	public void stopCrypto1() {
		myRfid.stopCrypto1();
	}

	/**
	 * 复位设备
	 */
	@Override
	public boolean resetDevice() {
		boolean isSuccess;
		isSuccess=myRfid.initMyRfid();
		isSuccess=stepperMotor.resetStepperMotor();
		return isSuccess;
	}

@Override
	public DeviceInfor getDeviceInfor() {
		return new DeviceInfor();
	}
	
	@Override
	public boolean saveDeviceInfor(DeviceInfor deviceInfor) {
		return true;
	}
	
	@Override
	public DeviceStateDate getDeviceStateDate() {
		return new DeviceStateDate();
	}
	
	@Override
	public EnviroDate getEnviroDate() {
		return new EnviroDate();
	}
	
}
