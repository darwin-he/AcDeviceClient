package components.stepper_motor;

/**
 * @author darwin_he
 * @date 2019/5/9 22:45
 */
public class StepperState {
    private boolean isTurning;
    private boolean isOpened;
    private boolean isClosed;

    public boolean isTurning() {
        return isTurning;
    }

    public void setTurning(boolean turning) {
        isTurning = turning;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
