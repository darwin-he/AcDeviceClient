/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package components.grating_sensor;

import com.pi4j.io.gpio.Pin;

public class GratingSensorPin {
    private Pin gratingSensorPin;

    public GratingSensorPin(Pin gratingSensorPin) {
        this.gratingSensorPin = gratingSensorPin;
    }

    public Pin getGratingSensorPin() {
        return gratingSensorPin;
    }

    public void setGratingSensorPin(Pin gratingSensorPin) {
        this.gratingSensorPin = gratingSensorPin;
    }
}
