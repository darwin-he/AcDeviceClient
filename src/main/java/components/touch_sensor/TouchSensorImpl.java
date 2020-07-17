package components.touch_sensor;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * @author darwin_he
 * @date 2019/4/12 17:25
 */
public class TouchSensorImpl implements TouchSensor {

    private GpioPinDigitalInput touchSensorDigitalPin;

    public TouchSensorImpl(TouchSensorPin touchSensorPin){
        GpioController gpioController= GpioFactory.getInstance();
        touchSensorDigitalPin=gpioController.provisionDigitalInputPin(touchSensorPin.getPin(),"TouchSensor", PinPullResistance.PULL_DOWN);
        touchSensorDigitalPin.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * 设置模块行为变化监听函数
     * @param listener
     */
    @Override
    public void setListener(GpioPinListenerDigital listener) {
        touchSensorDigitalPin.removeAllListeners();
        if (listener != null) {
            touchSensorDigitalPin.addListener(listener);
        }
    }

    /**
     * 获取触摸传感器的状态
     */
    @Override
    public PinState getState() {
        return touchSensorDigitalPin.getState();
    }
    
}
