package device.StepperMotor;
/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */

import com.pi4j.io.gpio.Pin;

public class StepperMotorPin {
    private final Pin PIN_IN1;
    private final Pin PIN_IN2;
    private final Pin PIN_IN3;
    private final Pin PIN_IN4;
    public StepperMotorPin(Pin PIN_IN1,Pin PIN_IN2,Pin PIN_IN3,Pin PIN_IN4){
        this.PIN_IN1=PIN_IN1;
        this.PIN_IN2=PIN_IN2;
        this.PIN_IN3=PIN_IN3;
        this.PIN_IN4=PIN_IN4;
    }

public Pin getPIN_IN1() {
    return PIN_IN1;
}

public Pin getPIN_IN2() {
    return PIN_IN2;
}

public Pin getPIN_IN3() {
    return PIN_IN3;
}

public Pin getPIN_IN4() {
    return PIN_IN4;
}
}
