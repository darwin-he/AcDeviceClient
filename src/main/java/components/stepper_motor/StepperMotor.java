/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package components.stepper_motor;

public interface StepperMotor {

	/**
	 * 开门
	 * @param onTurned
	 */
	void openDoor(OnTurned onTurned);
	
	/**
	 * 关门
	 * @param onTurned
	 */
	void closeDoor(OnTurned onTurned);
	
	StepperState getState();

    /**
     * 步进电机复位函数
     */
	 boolean reset();

}
