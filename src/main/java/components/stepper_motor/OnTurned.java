package components.stepper_motor;

/**
 * @author darwin_he
 * @date 2019/4/13 2:00
 */
public interface OnTurned {
    /**
     * 步进电机转动回调函数
     * @param isSucceed   是否成功转动
     */
    void onTurned(boolean isSucceed);
}
