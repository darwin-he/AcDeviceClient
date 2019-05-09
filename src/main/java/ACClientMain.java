import NetService.MyNetService;
import NetService.MyNetServiceImpl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.Console;
import DeviceCenter.ACCDeviceCenter;
import DeviceCenter.ACCDeviceCenterImpl;
import device.GraSensorModule.GraSensorModulePin;
import device.RfidSensorModule.RfidPin;
import device.StepperMotor.StepperMotorPin;
import device.TouchSensor.TouchSensorPin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */

public class ACClientMain {
    
    private static final String url="http://192.168.191.1:8080/websocket";
    
    public static void main(String args[]) throws IOException {
        final Console console = new Console();
        // allow for user to exit program using CTRL-C
        console.promptForExit();
        console.title("<-- The ACCilent Application -->", " is starting");
        
        GpioController gpioController= GpioFactory.getInstance();
        ACCDeviceCenter deviceCenter=new ACCDeviceCenterImpl();
        URI uri=null;
        try {
            uri=new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        MyNetService myNetService= new MyNetServiceImpl(uri,deviceCenter);;
        try {
            myNetService.connectToService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        console.title("<-- The ACCilent Application -->", "started");
        while (console.isRunning()){
          //Do something
        }
        console.title("<-- The ACCilent Application -->", "GoodBye");
        
        myNetService.disConnect();
        gpioController.shutdown();
    }

}
