package WebSocket.vo;

/**
 * @author darwin_he
 * @date 2019/5/9 20:28
 */
public class DeviceInfor {
	private String deviceNumber;
	private String deviceName;
	private String osName;
	private String runTimeEnviro;
	private String memorySize;
	private String remainderMemory;

	public String getDeviceNumber() {
		return deviceNumber;
	}
	
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getOsName() {
		return osName;
	}
	
	public void setOsName(String osName) {
		this.osName = osName;
	}
	
	public String getRunTimeEnviro() {
		return runTimeEnviro;
	}
	
	public void setRunTimeEnviro(String runTimeEnviro) {
		this.runTimeEnviro = runTimeEnviro;
	}
	
	public String getMemorySize() {
		return memorySize;
	}
	
	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}
	
	public String getRemainderMemory() {
		return remainderMemory;
	}
	
	public void setRemainderMemory(String remainderMemory) {
		this.remainderMemory = remainderMemory;
	}
}
