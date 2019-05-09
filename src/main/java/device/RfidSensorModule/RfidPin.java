package device.RfidSensorModule;

import com.pi4j.io.gpio.Pin;

/**
 * @author darwin_he
 * @date 2019/4/16 0:18
 */
public class RfidPin {
    private Pin rfidResetPin;
    
    public RfidPin(Pin rfidResetPin){
        this.rfidResetPin=rfidResetPin;
    }

    public Pin getRfidResetPin() {
        return rfidResetPin;
    }
}
