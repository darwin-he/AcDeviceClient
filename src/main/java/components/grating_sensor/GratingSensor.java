/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package components.grating_sensor;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public interface GratingSensor {
    
    /**
     * 获取光栅传感器的状态
     */
    PinState getState();

    /**
     * 设置光栅传感器监听函数
     * @param listener
     */
    void setListener(GpioPinListenerDigital listener);
    
}
