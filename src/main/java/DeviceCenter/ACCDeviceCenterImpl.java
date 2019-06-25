package DeviceCenter;

import WebSocket.vo.DeviceInfor;
import WebSocket.vo.DeviceStateDate;
import WebSocket.vo.EnviroDate;
import WebSocket.vo.UserCard;
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
import utils.TimeUtil;

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
	
	//自动开关门监听回调
	private TurnBack doorOpenBack;
	private TurnBack doorCloseBack;
	
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
			if (gpioPinDigitalStateChangeEvent.getState().isHigh())
				openDoor(doorOpenBack);
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
	 */
	@Override
	public void openDoor(TurnBack back) {
		stepperMotor.openDoor(isSuccessed -> {
			//成功开门后开启通过检测
			if (isSuccessed){
				startPassCheck();
			}
			//执行回调函数
			if (back!=null)
				back.turnFinshed(isSuccessed);
		});
	}

	@Override
	public void setAutoOpenOrCloseListener(TurnBack autoOpen, TurnBack autoClose) {
		doorOpenBack=autoOpen;doorCloseBack=autoClose;
	}

	/**
	 * 开启通过检测
	 */
	private void startPassCheck(){
		graSensorModule.setGraSensorModuleListener(gpioPinDigitalStateChangeEvent -> {
			//无物品则为低电平，这里再次变为低电平说明有物体已经通过
			if (gpioPinDigitalStateChangeEvent.getState().isLow()){
				//关闭通过检测
				graSensorModule.setGraSensorModuleListener(null);
				//关门
				closeDoor(doorCloseBack);
			}
		});
	}
	
	/**
	 * 关门
	 */
	@Override
	public void closeDoor(TurnBack back) {
		stepperMotor.closeDoor(isSuccessed -> {
			if (back!=null)
				back.turnFinshed(isSuccessed);
		});
	}
	

	/**
	 * 寻卡
	 * @return
	 */
	@Override
	public StatusCode searchCard() throws IOException {
		return myRfid.searchCard();
	}

	/**
	 * 写入块数据
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
	 * @param blockAddr
	 * @return 返回16字节数据
	 */
	@Override
	public byte[] readBlockData(byte blockAddr) throws IOException {
		return myRfid.readBlockData(blockAddr);
	}
	
	@Override
	public boolean resetDevice() {
		boolean isSuccess;
		isSuccess=myRfid.initMyRfid();
		isSuccess=stepperMotor.resetStepperMotor();
		return isSuccess;
	}

	@Override
	public String getDeviceNumber() {
		return "10010103403";
	}

	@Override
	public DeviceInfor getDeviceInfor() {
		DeviceInfor deviceInfor=new DeviceInfor();
		deviceInfor.setDeviceNumber(getDeviceNumber());
		deviceInfor.setDeviceName("阿尔法智能门禁终端");
		deviceInfor.setOsName("Raspbian");
		deviceInfor.setMemorySize("16GB");
		deviceInfor.setRemainderMemory("8.35G");
		deviceInfor.setRunTimeEnviro("JVM(64)");
		return deviceInfor;
	}
	
	@Override
	public DeviceStateDate getDeviceStateDate() {
		DeviceStateDate stateDate=new DeviceStateDate();
		stateDate.setDoorState("正常");
		stateDate.setRfidState("正常");
		stateDate.setGraState("正常");
		stateDate.setTouchState("正常");
		stateDate.setEnviroState("正常");
		return stateDate;
	}
	
	@Override
	public EnviroDate getEnviroDate() {
		EnviroDate enviroDate=new EnviroDate();
		enviroDate.setTemperature(26.5f);
		enviroDate.setHumidity(21.3f);
		enviroDate.setLightIntensity(36256);
		enviroDate.setTime(TimeUtil.getCurrentTime());
		return enviroDate;
	}
	
}
