package components.touch_sensor;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * @author darwin_he
 * @date 2019/4/12 17:20
 */
public interface TouchSensor {

    /**
     * 获取触摸传感器的状态
     */
    PinState getState();

    /**
     * 设置模块行为变化监听函数
     * @param listener
     */
    void setListener(GpioPinListenerDigital listener);
    
}
