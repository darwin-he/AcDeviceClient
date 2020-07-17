package components.rfid_reader.mfrc522;

import com.pi4j.io.gpio.Pin;

/**
 * @author darwin_he
 * @date 2019/4/16 0:18
 */
public class RestPin {
    private Pin pin;
    
    public RestPin(Pin pin){
        this.pin = pin;
    }

    public Pin getPin() {
        return pin;
    }
}
