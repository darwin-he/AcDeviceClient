import device_service.DeviceService;
import device_service.DeviceServiceImpl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.util.Console;
import control_center.ControlCenter;
import control_center.ControlCenterImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */

public class ACDevice {
    
    private static final String SERVICE_ADDR ="http://192.168.1.107:8080/LocalService";
    
    public static void main(String args[]) throws IOException {
        final Console console = new Console();
        // allow for user to exit program using CTRL-C
        console.promptForExit();
        console.title("<-- The ACCilent Application -->", " is starting");
        
        GpioController gpioController= GpioFactory.getInstance();
        ControlCenter controlCenter=new ControlCenterImpl();

        URI uri=null;
        try {
            uri=new URI(SERVICE_ADDR);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        DeviceService deviceService= new DeviceServiceImpl(uri);
        deviceService.start(controlCenter);

        console.println("<-- The ACCilent Application -->", "Started");
        while (console.isRunning()){
          //Do something
        }

        console.println("<-- The ACCilent Application -->", "GoodBye");
        
        deviceService.shutdown();
        gpioController.shutdown();
    }

}
