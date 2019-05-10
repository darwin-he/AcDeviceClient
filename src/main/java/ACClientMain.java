import WebSocket.Local.LocalClient;
import WebSocket.Local.LocalClientImpl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.util.Console;
import DeviceCenter.ACCDeviceCenter;
import DeviceCenter.ACCDeviceCenterImpl;

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
        LocalClient myNetService= new LocalClientImpl(uri);;
        try {
            myNetService.connectToService(deviceCenter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        console.println("<-- The ACCilent Application -->", "started");
        while (console.isRunning()){
          //Do something
        }
        console.println("<-- The ACCilent Application -->", "GoodBye");
        
        myNetService.disConnect();
        gpioController.shutdown();
    }

}
