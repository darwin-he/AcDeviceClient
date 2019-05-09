package device.StepperMotor;

/**
 * @author darwin_he
 * @date 2019/5/9 22:45
 */
public class StepperState {
	private boolean isTurnning;
	private boolean isOpend;
	private boolean isClosed;

public boolean isTurnning() {
	return isTurnning;
}

public void setTurnning(boolean turnning) {
	isTurnning = turnning;
}

public boolean isOpend() {
	return isOpend;
}

public void setOpend(boolean opend) {
	isOpend = opend;
}

public boolean isClosed() {
	return isClosed;
}

public void setClosed(boolean closed) {
	isClosed = closed;
}
}
