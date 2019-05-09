package device.TouchSensor;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author darwin_he
 * @date 2019/4/12 17:26
 */
public class TouchSensorPin {
    private final Pin touchSensorPin;
    public TouchSensorPin(Pin touchSensorPin){
        this.touchSensorPin=touchSensorPin;
    }

public Pin getTouchSensorPin() {
    return touchSensorPin;
}
}
