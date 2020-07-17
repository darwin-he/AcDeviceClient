package device_service.dto;

/**
 * @author darwin_he
 * @date 2019/5/9 19:58
 */
public class DeviceState {
    private String doorState;
    private String rfidState;
    private String graState;
    private String enviroState;
    private String touchState;

    public String getEnviroState() {
        return enviroState;
    }

    public void setEnviroState(String enviroState) {
        this.enviroState = enviroState;
    }

    public String getDoorState() {
        return doorState;
    }

    public void setDoorState(String doorState) {
        this.doorState = doorState;
    }

    public String getRfidState() {
        return rfidState;
    }

    public void setRfidState(String rfidState) {
        this.rfidState = rfidState;
    }

    public String getGraState() {
        return graState;
    }

    public void setGraState(String graState) {
        this.graState = graState;
    }

    public String getTouchState() {
        return touchState;
    }

    public void setTouchState(String touchState) {
        this.touchState = touchState;
    }
}
