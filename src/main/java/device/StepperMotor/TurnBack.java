package device.StepperMotor;

/**
 * @author darwin_he
 * @date 2019/4/13 2:00
 */
public interface TurnBack {
    /**
     * 步进电机转动回调函数
     * @param isSuccessed   是否成功转动
     */
    void turnFinshed(boolean isSuccessed);
}
