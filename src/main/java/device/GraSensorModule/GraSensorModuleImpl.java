/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package device.GraSensorModule;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GraSensorModuleImpl implements GraSensorModule {
    
    private GpioPinDigitalInput gpio;
    
    /**
     * 光栅模块对象构造函数
     */
    public GraSensorModuleImpl(GraSensorModulePin modulePin){
        GpioController gpioController= GpioFactory.getInstance();
        gpio=gpioController.provisionDigitalInputPin(modulePin.getGratingSensorPin(),"GraSensorPIN", PinPullResistance.PULL_DOWN);
        gpio.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * 获取出光栅传感器的状态
     */
    @Override
    public PinState readGraSensorModuleState() {
        return gpio.getState();
    }

    /**
     * 设置光栅传感器监听函数
     *
     * @param listener
     */
    @Override
    public void setGraSensorModuleListener(GpioPinListenerDigital listener) {
        if (listener!=null){
            gpio.removeAllListeners();
            gpio.addListener(listener);
        }else {
            gpio.removeAllListeners();
        }
    }
    
}
