package components.rfid_reader;

import components.rfid_reader.mfrc522.UID;

/**
 * @author darwin_he
 * @date 2019/5/9 17:50
 */
public interface OnSearchedCard {
    /**
     * 寻卡成功时的回调函数
     */
    void onSearchedCard(UID uid);
}
