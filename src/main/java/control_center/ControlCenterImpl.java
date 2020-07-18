package control_center;

import device_service.dto.DeviceInfo;
import device_service.dto.DeviceState;
import device_service.dto.EnviroInfo;
import device_service.dto.UserCard;
import com.pi4j.io.gpio.RaspiPin;
import components.grating_sensor.GratingSensorImpl;
import components.grating_sensor.GratingSensorPin;
import components.rfid_reader.mfrc522.StatusCode;
import components.rfid_reader.mfrc522.UID;
import components.rfid_reader.MyRfidReaderImpl;
import components.rfid_reader.mfrc522.RestPin;
import components.stepper_motor.StepperMotorImpl;
import components.stepper_motor.StepperMotorPin;
import components.stepper_motor.OnTurned;
import components.touch_sensor.TouchSensorImpl;
import components.touch_sensor.TouchSensorPin;
import utils.HexUtil;
import utils.TimeUtil;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
public class ControlCenterImpl implements ControlCenter {
	
	private GratingSensorImpl graSensorModule;
	private MyRfidReaderImpl myRfid;
	private StepperMotorImpl stepperMotor;
	private TouchSensorImpl touchSensor;

	private GratingSensorPin gratingSensorPin;
	private RestPin rfidResetPin;
	private StepperMotorPin stepperMotorPin;
	private TouchSensorPin touchSensorPin;
	
	//自动开关门监听回调
	private OnTurned doorOpenBack;
	private OnTurned doorCloseBack;
	
	public ControlCenterImpl() throws IOException {
		gratingSensorPin =new GratingSensorPin(RaspiPin.GPIO_25);
		rfidResetPin =new RestPin(RaspiPin.GPIO_06);
		stepperMotorPin=new StepperMotorPin(RaspiPin.GPIO_00, RaspiPin.GPIO_02,RaspiPin.GPIO_03,RaspiPin.GPIO_04);
		touchSensorPin=new TouchSensorPin(RaspiPin.GPIO_05);
		
		graSensorModule=new GratingSensorImpl(gratingSensorPin);
		myRfid=new MyRfidReaderImpl(rfidResetPin);
		stepperMotor=new StepperMotorImpl(stepperMotorPin);
		touchSensor=new TouchSensorImpl(touchSensorPin);
		//开启开门检测
		touchSensor.setListener(gpioPinDigitalStateChangeEvent -> {
			//由低电平变为高电平说明正有物品触摸在传感器上
			if (gpioPinDigitalStateChangeEvent.getState().isHigh())
				openDoor(doorOpenBack);
		});
	}
	

	/**
	 * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
	 * @return
	 */
	@Override
	public UserCard searchCardOnce() {
		UID uid=myRfid.searchOnce();
		UserCard userCard =null;
		if (uid!=null){
			userCard =new UserCard();
			userCard.setUserCard(HexUtil.encodeHexString(uid.getUidBytes()));
		}
		return userCard;
	}

	/**
	 * 开启自动寻卡
	 * @param searchBack 寻卡成功时的回调函数
	 */
	@Override
	public void startAutoSearchCard(AutoSearchBack searchBack) {
		myRfid.startAutoSearch(uid -> {
			UserCard userCard =new UserCard();
			userCard.setUserCard(HexUtil.encodeHexString(uid.getUidBytes()));
			searchBack.onSearchOnUserCard(userCard);
		});
	}
	
	/**
	 * 停止自动寻卡
	 *
	 * @return
	 */
	@Override
	public void stopAutoSearchCard() {
		myRfid.stopAutoSearch();
	}
	
	/**
	 * 开门
	 */
	@Override
	public void openDoor(OnTurned back) {
		stepperMotor.openDoor(isSuccessed -> {
			//成功开门后开启通过检测
			if (isSuccessed){
				startPassCheck();
			}
			//执行回调函数
			if (back!=null)
				back.onTurned(isSuccessed);
		});
	}

	@Override
	public void setDoorListener(OnTurned autoOpen, OnTurned autoClose) {
		doorOpenBack=autoOpen;doorCloseBack=autoClose;
	}

	/**
	 * 开启通过检测
	 */
	private void startPassCheck(){
		graSensorModule.setListener(gpioPinDigitalStateChangeEvent -> {
			//无物品则为低电平，这里再次变为低电平说明有物体已经通过
			if (gpioPinDigitalStateChangeEvent.getState().isLow()){
				//关闭通过检测
				graSensorModule.setListener(null);
				//关门
				closeDoor(doorCloseBack);
			}
		});
	}
	
	/**
	 * 关门
	 */
	@Override
	public void closeDoor(OnTurned back) {
		stepperMotor.closeDoor(isSuccessed -> {
			if (back!=null)
				back.onTurned(isSuccessed);
		});
	}

	/**
	 * 写入块数据
	 * @param blockAddr
	 * @param buffer    必须是16字节数据
	 * @return
	 */
	@Override
	public StatusCode writeBlockDataToCard(byte blockAddr, byte[] buffer) throws IOException {
		return myRfid.writeBlockData(blockAddr,buffer);
	}

	/**
	 * 读块数据
	 * @param blockAddr
	 * @return 返回16字节数据
	 */
	@Override
	public byte[] readBlockDataFromCard(byte blockAddr) throws IOException {
		return myRfid.readBlockData(blockAddr);
	}
	
	@Override
	public boolean reset() {
		boolean isSuccess;
		isSuccess=myRfid.reset();
		isSuccess=stepperMotor.reset();
		return isSuccess;
	}

	@Override
	public String getDeviceNumber() {
		return "10010103403";
	}

	@Override
	public DeviceInfo getDeviceInfor() {
		DeviceInfo deviceInfo =new DeviceInfo();
		deviceInfo.setDeviceNumber(getDeviceNumber());
		deviceInfo.setDeviceName("阿尔法智能门禁终端");
		deviceInfo.setOsName("Raspbian");
		deviceInfo.setMemorySize("16GB");
		deviceInfo.setRemainderMemory("8.35G");
		deviceInfo.setRunTimeEnviro("JVM(64)");
		return deviceInfo;
	}
	
	@Override
	public DeviceState getDeviceStateDate() {
		DeviceState stateDate=new DeviceState();
		stateDate.setDoorState("正常");
		stateDate.setRfidState("正常");
		stateDate.setGraState("正常");
		stateDate.setTouchState("正常");
		stateDate.setEnviroState("正常");
		return stateDate;
	}
	
	@Override
	public EnviroInfo getEnviroDate() {
		EnviroInfo enviroInfo =new EnviroInfo();
		enviroInfo.setTemperature(26.5f);
		enviroInfo.setHumidity(21.3f);
		enviroInfo.setLightIntensity(36256);
		enviroInfo.setTime(TimeUtil.getCurrentTime());
		return enviroInfo;
	}
	
}
