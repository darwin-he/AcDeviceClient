/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package device.GraSensorModule;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public interface GraSensorModule {
    
    /**
     * 获取光栅传感器的状态
     */
    PinState readGraSensorModuleState();

    /**
     * 设置光栅传感器监听函数
     * @param listener
     */
    void setGraSensorModuleListener(GpioPinListenerDigital listener);
    
}
