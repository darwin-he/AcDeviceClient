package device.RfidSensorModule;

import device.RfidSensorModule.Mfrc522.StatusCode;
import device.RfidSensorModule.Mfrc522.UID;

/**
 * @author darwin_he
 * @date 2019/5/9 17:50
 */
public interface OnSearchCard {
/**
 * 自动寻卡成功时的回调函数
 */
void onSearchCard(UID uid);
}
