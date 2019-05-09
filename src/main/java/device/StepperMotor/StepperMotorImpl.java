/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package device.StepperMotor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class StepperMotorImpl implements StepperMotor {

    //记录当前点击的转动状态
    private boolean isTurnning=false;
    private boolean isOpended=false;
    private boolean isColsed=true;
    
    //相位A
    private GpioPinDigitalOutput gpioOut1;
    //相位B
    private GpioPinDigitalOutput gpioOut2;
    //相位C
    private GpioPinDigitalOutput gpioOut3;
    //相位D
    private GpioPinDigitalOutput gpioOut4;
    
    public StepperMotorImpl(StepperMotorPin stepperMotorPin){
        GpioController gpioController= GpioFactory.getInstance();
        gpioOut1=gpioController.provisionDigitalOutputPin(stepperMotorPin.getPIN_IN1(),"Motro_IN1", PinState.LOW);
        gpioOut2=gpioController.provisionDigitalOutputPin(stepperMotorPin.getPIN_IN2(),"Motro_IN2", PinState.LOW);
        gpioOut3=gpioController.provisionDigitalOutputPin(stepperMotorPin.getPIN_IN3(),"Motro_IN3", PinState.LOW);
        gpioOut4=gpioController.provisionDigitalOutputPin(stepperMotorPin.getPIN_IN4(),"Motro_IN4", PinState.LOW);
        gpioOut1.setShutdownOptions(true, PinState.LOW);
        gpioOut2.setShutdownOptions(true, PinState.LOW);
        gpioOut3.setShutdownOptions(true, PinState.LOW);
        gpioOut4.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * 开门
     *
     * @param onBack
     */
    @Override
    public void openDoor(TurnBack onBack) {
        if (!isReadyToManipulation()){
            onBack.turnFinshed(false);
            return;
        }
        if (isOpended){
            onBack.turnFinshed(true);
            return;
        }
        isTurnning=true;
        uniformTurnCW(200,200000,onBack);
        isColsed=false;
        isOpended=true;
        isTurnning=false;
    }
    
    /**
     * 关门
     *
     * @param onBack
     */
    @Override
    public void closeDoor(TurnBack onBack) {
        if (!isReadyToManipulation()){
            onBack.turnFinshed(false);
            return;
        }
        if (isColsed){
            onBack.turnFinshed(true);
            return;
        }
        isTurnning=true;
        uniformTurnCCW(200,200000,onBack);
        isColsed=true;
        isOpended=false;
        isOpended=false;
    }

    @Override
    public StepperState getStepperState() {
        StepperState stepperState=new StepperState();
        stepperState.setClosed(isColsed);
        stepperState.setOpend(isOpended);
        stepperState.setTurnning(isTurnning);
        return stepperState;
    }

/**
     * 复位步进电机
     * @return
     */
    @Override
    public boolean resetStepperMotor() {
        return true;
    }

    /**
     * 顺时针匀速转动
     * @param frequency   输出到步进电机的脉冲频率值越小，频率越大，转速越快(最小200000)
     * @param stepCount   转动的步数
     * @param onBack      转动完成回调函数
     */
    private void uniformTurnCW(int stepCount, long frequency, TurnBack onBack) {
        setAllGpioPinToLow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=stepCount;i>0;i--){
                    turnOneStepCW(frequency);
                }
                if (onBack!=null)
                    onBack.turnFinshed(true);
            }
        }).start();
    }

    /**
     * 逆时针匀速转动
     * @param frequency   输出到步进电机的脉冲频率值越小，频率越大，转速越快(最小200000)
     * @param stepCount   转动的步数
     * @param onBack      转动完成回调函数
     */
    private void uniformTurnCCW(int stepCount, long frequency, TurnBack onBack) {
        setAllGpioPinToLow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=stepCount;i>0;i--){
                    turnOneStepCCW(frequency);
                }
                if (onBack!=null)
                    onBack.turnFinshed(true);
            }
        }).start();
    }

    /**
     * 判断模块是否可以被操作
     * @return
     */
    private boolean isReadyToManipulation(){
        if (gpioOut1==null||gpioOut2==null||gpioOut3==null||gpioOut4==null)
            return false;
        return true;
    }

    /**
     * 将所有GPIO引脚设置为低电压
     */
    private void setAllGpioPinToLow(){
        gpioOut1.low();
        gpioOut2.low();
        gpioOut3.low();
        gpioOut4.low();
    }

    /**
     * 顺时针转动一步
     * @param frequency 输出到步进电机的脉冲频率值越小，频率越大，转速越快(最小200000)
     */
    private void turnOneStepCW(long frequency){
        for (int i=1;i<9;i++){
            metronome(i,frequency);
        }
    }

    /**
     * 逆时针转动一步
     * @param frequency  输出到步进电机的脉冲频率值越小，频率越大，转速越快(最小200000)
     */
    private void turnOneStepCCW(long frequency){
        for (int i=8;i>0;i--){
            metronome(i,frequency);
        }
    }

    /**
     * 节拍器
     * @param whichStep 第几拍
     * @param frequency  输出到步进电机的脉冲频率值越小，频率越大，转速越快(最小200000)
     */
    private void metronome (int whichStep,long frequency){
        switch (whichStep){
            case 1:
                beat1();
                break;
            case 2:
                beat2();
                break;
            case 3:
                beat3();
                break;
            case 4:
                beat4();
                break;
            case 5:
                beat5();
                break;
            case 6:
                beat6();
                break;
            case 7:
                beat7();
                break;
            case 8:
                beat8();
                break;
        }
        for (int i=0;i<frequency;i++){
        }
    }

    /**
     * 四相八拍第1拍（顺时针）
     */
    private void beat1(){
        gpioOut1.high();
        gpioOut2.low();
        gpioOut3.low();
        gpioOut4.low();
    }

    /**
     * 四相八拍第2拍（顺时针）
     */
    private void beat2(){
        gpioOut1.high();
        gpioOut2.high();
        gpioOut3.low();
        gpioOut4.low();
    }

    /**
     * 四相八拍第3拍（顺时针）
     */
    private void beat3(){
        gpioOut1.low();
        gpioOut2.high();
        gpioOut3.low();
        gpioOut4.low();
    }

    /**
     * 四相八拍第4拍（顺时针）
     */
    private void beat4(){
        gpioOut1.low();
        gpioOut2.high();
        gpioOut3.high();
        gpioOut4.low();
    }

    /**
     * 四相八拍第5拍（顺时针）
     */
    private void beat5(){
        gpioOut1.low();
        gpioOut2.low();
        gpioOut3.high();
        gpioOut4.low();
    }

    /**
     * 四相八拍第6拍（顺时针）
     */
    private void beat6(){
        gpioOut1.low();
        gpioOut2.low();
        gpioOut3.high();
        gpioOut4.high();
    }

    /**
     * 四相八拍第7拍（顺时针）
     */
    private void beat7(){
        gpioOut1.low();
        gpioOut2.low();
        gpioOut3.low();
        gpioOut4.high();
    }

    /**
     * 四相八拍第8拍（顺时针）
     */
    private void beat8(){
        gpioOut1.high();
        gpioOut2.low();
        gpioOut3.low();
        gpioOut4.high();
    }

}
