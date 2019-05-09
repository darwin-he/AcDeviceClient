package device.RfidSensorModule.Mfrc522;

import utils.Hex;

import java.util.Arrays;
import java.util.List;

/**
 * @author darwin_he
 * @date 2019/4/22 0:58
 */
public class UID {
    /** Number of bytes in the UID. 4, 7 or 10. */
    private byte[] uidBytes;
    /** The SAK (Select acknowledge) byte returned from the PICC after successful selection. */
    private byte sak;

    public UID(List<Byte> bytes, byte sak) {
        uidBytes = new byte[bytes.size()];
        for (int i=0; i<bytes.size(); i++) {
            uidBytes[i] = bytes.get(i).byteValue();
        }
        this.sak = sak;
    }

    public int getSize() {
        return uidBytes.length;
    }

    public byte getUidByte(int index) {
        return uidBytes[index];
    }

    public byte[] getUidBytes() {
        return uidBytes;
    }

    public byte getSak() {
        return sak;
    }

    @Override
    public String toString() {
        return "UID [uidBytes=" + Hex.encodeHexString(uidBytes) + ", sak=" + sak + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(uidBytes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UID other = (UID) obj;
        if (!Arrays.equals(uidBytes, other.uidBytes))
            return false;
        return true;
    }

    public PiccType getType() {
        return PiccType.forId(sak);
    }
}
