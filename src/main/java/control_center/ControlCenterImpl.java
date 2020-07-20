package control_center;

import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.spi.SpiChannel;
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
    private MyRfidReaderImpl outsideRfid;
    private MyRfidReaderImpl insideRfid;
    private StepperMotorImpl stepperMotor;
    private TouchSensorImpl touchSensor;

    private GratingSensorPin gratingSensorPin;
    private RestPin outSideRfidResetPin;
    private RestPin insideRfidResetPin;
    private StepperMotorPin stepperMotorPin;
    private TouchSensorPin touchSensorPin;

    public ControlCenterImpl() throws IOException {
        gratingSensorPin = new GratingSensorPin(RaspiPin.GPIO_25);
        outSideRfidResetPin = new RestPin(RaspiPin.GPIO_06);
        insideRfidResetPin = new RestPin(RaspiPin.GPIO_21);
        stepperMotorPin = new StepperMotorPin(RaspiPin.GPIO_00, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04);
        touchSensorPin = new TouchSensorPin(RaspiPin.GPIO_05);

        graSensorModule = new GratingSensorImpl(gratingSensorPin);
        outsideRfid = new MyRfidReaderImpl(outSideRfidResetPin, SpiChannel.CS0);
        insideRfid = new MyRfidReaderImpl(insideRfidResetPin, SpiChannel.CS1);
        stepperMotor = new StepperMotorImpl(stepperMotorPin);
        touchSensor = new TouchSensorImpl(touchSensorPin);
    }

    @Override
    public void enableHumanHandle(OnTurned onOpened, OnTurned onClosed) {
        touchSensor.setListener(gpioPinDigitalStateChangeEvent -> {
            //由低电平变为高电平说明正有物品触摸在传感器上
            if (gpioPinDigitalStateChangeEvent.getState().isHigh())
                openDoor(onOpened, onClosed);
        });
    }

    public void forbidHumanHandle() {
        touchSensor.setListener(null);
    }

    /**
     * 手动寻卡一次，时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
     *
     * @return
     */
    @Override
    public UserCard searchCardOnce() {
        insideRfid.stopAutoSearch();
        outsideRfid.stopAutoSearch();
        UID uid = outsideRfid.searchOnce();
        UserCard userCard = null;
        if (uid != null) {
            userCard = new UserCard();
            userCard.setUserCard(HexUtil.encodeHexString(uid.getUidBytes()));
        }
        return userCard;
    }

    /**
     * 开启自动寻卡
     *
     * @param outsideRfidSearchBack 寻卡成功时的回调函数
     */
    @Override
    public void startAutoSearchCard(AutoSearchBack outsideRfidSearchBack, AutoSearchBack insideRfidSearchBack) {
        outsideRfid.startAutoSearch(uid -> {
            UserCard userCard = new UserCard();
            userCard.setUserCard(HexUtil.encodeHexString(uid.getUidBytes()));
            if (outsideRfidSearchBack == null) return;
            outsideRfidSearchBack.onSearchOnUserCard(userCard);
        });

        insideRfid.startAutoSearch(uid -> {
            UserCard userCard = new UserCard();
            userCard.setUserCard(HexUtil.encodeHexString(uid.getUidBytes()));
            if (insideRfidSearchBack == null) return;
            insideRfidSearchBack.onSearchOnUserCard(userCard);
        });
    }

    /**
     * 停止自动寻卡
     *
     * @return
     */
    @Override
    public void stopAutoSearchCard() {
        outsideRfid.stopAutoSearch();
        insideRfid.stopAutoSearch();
    }

    /**
     * 开门
     */
    @Override
    public void openDoor(OnTurned onOpened, OnTurned onClosed) {
        stepperMotor.openDoor(isSuccessed -> {
            //成功开门后开启通过检测
            if (isSuccessed) {
                startPassCheck(onClosed);
            }
            //执行回调函数
            if (onOpened != null)
                onOpened.onTurned(isSuccessed);
        });
    }

    /**
     * 开启通过检测
     */
    private void startPassCheck(OnTurned onClosed) {
        graSensorModule.setListener(gpioPinDigitalStateChangeEvent -> {
            //无物品则为低电平，这里再次变为低电平说明有物体已经通过
            if (gpioPinDigitalStateChangeEvent.getState().isLow()) {
                //关闭通过检测
                graSensorModule.setListener(null);
                //关门
                closeDoor(onClosed);
            }
        });
    }

    /**
     * 关门
     */
    @Override
    public void closeDoor(OnTurned back) {
        stepperMotor.closeDoor(isSuccessed -> {
            if (back != null)
                back.onTurned(isSuccessed);
        });
    }

    /**
     * 写入块数据
     *
     * @param blockAddr
     * @param buffer    必须是16字节数据
     * @return
     */
    @Override
    public StatusCode writeBlockDataToCard(byte blockAddr, byte[] buffer) throws IOException {
        return outsideRfid.writeBlockData(blockAddr, buffer);
    }

    /**
     * 读块数据
     *
     * @param blockAddr
     * @return 返回16字节数据
     */
    @Override
    public byte[] readBlockDataFromCard(byte blockAddr) throws IOException {
        return outsideRfid.readBlockData(blockAddr);
    }

    @Override
    public void reset() {
        graSensorModule.setListener(null);
        outsideRfid.reset();
        insideRfid.reset();
        stepperMotor.reset();
    }

    @Override
    public String getDeviceNumber() {
        return "10010103403";
    }

    @Override
    public DeviceInfo getDeviceInfor() {
        DeviceInfo deviceInfo = new DeviceInfo();
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
        DeviceState stateDate = new DeviceState();
        stateDate.setDoorState("正常");
        stateDate.setRfidState("正常");
        stateDate.setGraState("正常");
        stateDate.setTouchState("正常");
        stateDate.setEnviroState("正常");
        return stateDate;
    }

    @Override
    public EnviroInfo getEnviroDate() {
        EnviroInfo enviroInfo = new EnviroInfo();
        enviroInfo.setTemperature(getRandom(20.0f, 35.0f));
        enviroInfo.setHumidity(getRandom(15.0f, 35.0f));
        enviroInfo.setLightIntensity(getRandom(2000, 4000));
        enviroInfo.setTime(TimeUtil.getCurrentTime());
        return enviroInfo;
    }

    private int getRandom(int min, int max) {
        return (int) (min + Math.random() * (max - min));
    }

    private float getRandom(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }

}
