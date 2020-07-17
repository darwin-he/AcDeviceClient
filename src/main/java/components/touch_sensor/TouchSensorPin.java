package components.touch_sensor;

import com.pi4j.io.gpio.Pin;

/**
 * @author darwin_he
 * @date 2019/4/12 17:26
 */
public class TouchSensorPin {
    private final Pin pin;

    public TouchSensorPin(Pin pin) {
        this.pin = pin;
    }

    public Pin getPin() {
        return pin;
    }
}
