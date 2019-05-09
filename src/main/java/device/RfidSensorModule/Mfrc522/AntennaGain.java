package device.RfidSensorModule.Mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 1:00
 */
public enum AntennaGain {
    DB_18A((byte) (0b000 << 4), 18),
    DB_23A((byte) (0b001 << 4), 23),
    DB_18B((byte) (0b010 << 4), 18),
    DB_23B((byte) (0b011 << 4), 23),
    DB_33((byte) (0b100 << 4), 33),
    DB_38((byte) (0b101 << 4), 38),
    DB_43((byte) (0b110 << 4), 43),
    DB_48((byte) (0b111 << 4), 48);

    private byte value;
    private int gainDb;

    private AntennaGain(byte value, int gainDb) {
        this.value = value;
        this.gainDb = gainDb;
    }

    public byte getValue() {
        return value;
    }

    public int getGainDb() {
        return gainDb;
    }

    public static AntennaGain forValue(byte value) {
        switch (value) {
            case 0b000:
                return DB_18A;
            case 0b001:
                return DB_23A;
            case 0b010:
                return DB_18B;
            case 0b011:
                return DB_23B;
            case 0b100:
                return DB_33;
            case 0b101:
                return DB_38;
            case 0b110:
                return DB_43;
            case 0b111:
                return DB_48;
        }
        return null;
    }
}
