package device.RfidSensorModule;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import device.RfidSensorModule.Mfrc522.MFRC522;
import device.RfidSensorModule.Mfrc522.StatusCode;
import device.RfidSensorModule.Mfrc522.UID;

import java.io.IOException;

/**
 * @author darwin_he
 * @date 2019/4/15 23:47
 */
public class MyRfidImpl implements MyRfid {
    
    private final MFRC522 mfrc522;
    private byte[] bufferATQA;//缓存卡片类型字节码，数组大小必须两个字节
    
    private boolean isContinueSearch;

    private final GpioPinDigitalOutput rfidRestPin;

    public MyRfidImpl(RfidPin rfidPin) throws IOException {
        SpiDevice spiDevice= SpiFactory.getInstance(SpiChannel.CS0,//树莓派对应的spi片选通道
                SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
        GpioController controller= GpioFactory.getInstance();
        rfidRestPin=controller.provisionDigitalOutputPin(rfidPin.getRfidResetPin(), "RFID_RESET", PinState.HIGH);
        rfidRestPin.setShutdownOptions(true, PinState.LOW);
        mfrc522=new MFRC522(spiDevice,rfidRestPin);
        bufferATQA=new byte[2];
        isContinueSearch=false;
    }

    /**
     * 复位Rfid模块,会关闭自动寻卡功能
     */
    @Override
    public boolean initMyRfid() {
        stopAutoSearchCard();
        try {
            mfrc522.init();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 手动寻卡一次,时间限制为8秒，执行此函数会关闭自动寻卡，需要重新启动自动寻卡功能。
     * @return
     */
    @Override
    public UID searchCardOneTime() {
        if (isContinueSearch){//关闭自动识别
            isContinueSearch=false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        UID uid=null;
        for (int i=0;i<8;i++){
            StatusCode statusCode=searchCard();
            if (statusCode==StatusCode.OK){
                uid=anticollAndSelect();
                if (uid!=null){
                    break;
                }else {
                    continue;
                }
            }else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return uid;
    }

    /**
     * 开启自动寻卡
     *
     * @param onSearchCard 寻卡成功时的回调函数
     */
    @Override
    public void startAutoSearchCard(OnSearchCard onSearchCard) {
        if (isContinueSearch)
            return;
        isContinueSearch=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isContinueSearch){
                    StatusCode statusCode=searchCard();
                    if (statusCode==StatusCode.OK){
                        UID uid=anticollAndSelect();
                        if (uid!=null){
                            onSearchCard.onSearchCard(uid);
                        }else {
                            continue;
                        }
                    }else {
                        try {
                            Thread.sleep(1500);
                        }catch (InterruptedException i){
                            isContinueSearch=false;
                        }
                    }
                }
            }
        }).start();
    }
    
    /**
     * 停止自动寻卡
     *
     * @return
     */
    @Override
    public boolean stopAutoSearchCard() {
        isContinueSearch=false;
        return true;
    }

    /**
     * 寻卡
     * @return
     */
    @Override
    public StatusCode searchCard(){
        try {
            return mfrc522.requestA(bufferATQA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.ERROR;
    }

    /**
     * 防冲撞及选卡
     *
     * @return  成功则返回UID否则返回null
     */
    @Override
    public UID anticollAndSelect(){
        try {
            return mfrc522.select();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证卡片的块密码
     *
     * @param authKeyA  是否是KeyA
     * @param blockAddr
     * @param key
     * @param uid
     * @return
     */
    @Override
    public StatusCode authenticate(boolean authKeyA, byte blockAddr, byte[] key, UID uid){
        try {
            return mfrc522.authenticate(authKeyA,blockAddr,key,uid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.ERROR;
    }

    /**
     * 写入块数据
     *
     * @param blockAddr
     * @param buffer    必须是16字节数据
     * @return
     */
    @Override
    public StatusCode writeBlockData(byte blockAddr, byte[] buffer){
        try {
            return mfrc522.mifareWrite(blockAddr,buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.ERROR;
    }

    /**
     * 读块数据
     *
     * @param blockAddr
     * @return 成功则返回16字节数据否则位null
     */
    @Override
    public byte[] readBlockData(byte blockAddr){
        try {
            return mfrc522.mifareRead(blockAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 唤醒卡片
     * @return
     */
    public StatusCode wakeupCard(){
        try {
            return mfrc522.wakeupA(bufferATQA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.ERROR;
    }

    /**
     * 命令卡片进入休眠状态
     *
     * @return
     */
    @Override
    public StatusCode haltCard() {
        try {
            return mfrc522.haltA();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.ERROR;
    }

    /**
     * 退出验证状态
     */
    @Override
    public void stopCrypto1() {
        try {
            mfrc522.stopCrypto1();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
