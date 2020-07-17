package components.rfid_reader.mfrc522;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

import java.io.IOException;


/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */

public class Mfrc522Test {
    
    private static byte cardBlockData[] = {0x12,0x34,0x56,0x78,(byte) 0xED,(byte)0xCB,(byte)0xA9,(byte)0x87,0x12,0x34,0x56,0x78,0x01,(byte)0xFE,0x01,(byte)0xFE};
    // SPI device
    public static SpiDevice spi = null;

    // ADC channel count
    //public static short ADC_CHANNEL_COUNT = 8;  // MCP3004=4, MCP3008=8

    // create Pi4J console wrapper/helper
    // (This is a utility class to abstract some of the boilerplate code)
    protected static final Console console = new Console();

    /**
     * Sample SPI Program
     *
     * @param args (none)
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String args[]) throws InterruptedException, IOException {

        // println() program title/header
        console.title("<-- The Pi4J Project -->", "SPI test program using MFRC522");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        GpioController gpioController= GpioFactory.getInstance();
        GpioPinDigitalOutput resetGpioPin=gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_06,"RFID_ResetPIN", PinState.LOW);
        resetGpioPin.setShutdownOptions(true, PinState.LOW);

        spi = SpiFactory.getInstance(SpiChannel.CS0,
                SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
        MFRC522 mfrc522=new MFRC522(spi,resetGpioPin);
        StatusCode statusCode;
        while (console.isRunning()){
            Thread.sleep(2000);
            //寻卡
            byte[] bufferATQA=new byte[2];
            statusCode=mfrc522.requestA(bufferATQA);
            if (statusCode.getCode()!= StatusCode.OK.getCode()){
                console.println("寻卡：err");
                continue;
            }else {
                console.println("寻卡："+ MFRC522.getPiccType(bufferATQA[0]).getName());
            }
            //防冲撞加选卡
            UID uid=mfrc522.select();
            if (uid==null){
                console.println("防冲撞加选卡：err");
                continue;
            }else {
                console.println("防冲撞加选卡："+ uid.toString()+"类型："+ MFRC522.getPiccType(uid.getSak()).getName());
            }
            //验证密码
            statusCode=mfrc522.authenticate(true, (byte) 0x01, MFRC522.DEFAULT_KEY,uid);
            if (statusCode.getCode()!= StatusCode.OK.getCode()){
                console.println("验证密码：err");
                continue;
            }else {
                console.println("验证密码：ok");
            }
            //写数据
            statusCode=mfrc522.mifareWrite((byte)1,cardBlockData);
            if (statusCode.getCode()!= StatusCode.OK.getCode()){
                console.println("写数据：err");
                //退出验证状态
                mfrc522.stopCrypto1();
                continue;
            }else {
                console.println("写数据：ok");
            }
            //读数据
            byte[] readData=mfrc522.mifareRead((byte)1);
            if (readData==null)
                console.println("读到的数据：err");
            else
                console.println("读到的数据:"+readData.toString()+"长度："+readData.length);
            mfrc522.stopCrypto1();
        }
        gpioController.shutdown();
    }

}
