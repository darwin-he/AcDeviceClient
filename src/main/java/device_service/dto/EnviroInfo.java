package device_service.dto;

/**
 * @author darwin_he
 * @date 2019/5/9 19:57
 */
public class EnviroInfo {
    private float temperature;
    private float humidity;
    private int lightIntensity;
    private String time;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(int lightIntensity) {
        this.lightIntensity = lightIntensity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
