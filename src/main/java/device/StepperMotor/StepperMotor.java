/**
 * @author darwin_he
 * @date 2019/4/10 21:54
 */
package device.StepperMotor;

public interface StepperMotor {

	/**
	 * 开门
	 * @param onBack
	 */
	void openDoor(TurnBack onBack);
	
	/**
	 * 关门
	 * @param onBack
	 */
	void closeDoor(TurnBack onBack);
	
	StepperState getStepperState();

    /**
     * 步进电机复位函数
     */
	 boolean resetStepperMotor();

}
