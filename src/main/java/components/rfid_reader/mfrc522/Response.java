package components.rfid_reader.mfrc522;

import java.util.Arrays;

/**
 * @author darwin_he
 * @date 2019/4/22 0:55
 */
public class Response {
    private StatusCode status;
    private byte[] backData;
    private int backLen;
    private byte validBits;

    public Response(StatusCode status) {
        this.status = status;
    }

    public Response(StatusCode status, byte[] backData) {
        this.status = status;
        this.backData = backData;
    }

    public Response(StatusCode status, byte[] backData, int backLen, byte validBits) {
        this.status = status;
        this.backData = backData;
        this.backLen = backLen;
        this.validBits = validBits;
    }

    public StatusCode getStatus() {
        return status;
    }

    public byte[] getBackData() {
        return backData;
    }

    public int getBackLen() {
        return backLen;
    }

    public byte getValidBits() {
        return validBits;
    }

    @Override
    public String toString() {
        return "Response [status=" + status + ", backData=" + Arrays.toString(backData) + ", backLen=" + backLen
                + ", validBits=" + validBits + "]";
    }
}
