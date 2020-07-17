package components.rfid_reader.mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 1:00
 */
public enum PiccType {
    UNKNOWN("Unknown type"),
    ISO_14443_4("PICC compliant with ISO/IEC 14443-4"),
    ISO_18092("PICC compliant with ISO/IEC 18092 (NFC)"),
    MIFARE_MINI("MIFARE Mini, 320 bytes"),
    MIFARE_1K("MIFARE 1KB"),
    MIFARE_4K("MIFARE 4KB"),
    MIFARE_UL("MIFARE Ultralight or Ultralight C"),
    MIFARE_PLUS("MIFARE Plus"),
    MIFARE_DESFIRE("MIFARE DESFire"),
    TNP3XXX("MIFARE TNP3XXX"),
    NOT_COMPLETE("SAK indicates UID is not complete.");

    private String name;

    private PiccType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PiccType forId(byte id) {
        switch (id) {
            case 0x04:	return NOT_COMPLETE;	// UID not complete
            case 0x09:	return MIFARE_MINI;
            case 0x08:	return MIFARE_1K;
            case 0x18:	return MIFARE_4K;
            case 0x00:	return MIFARE_UL;
            case 0x10:
            case 0x11:	return MIFARE_PLUS;
            case 0x01:	return TNP3XXX;
            case 0x20:	return ISO_14443_4;
            case 0x40:	return ISO_18092;
            default:	return UNKNOWN;
        }
    }
}
