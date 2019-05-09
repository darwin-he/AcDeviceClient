package device.RfidSensorModule.Mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 0:52
 */
public enum StatusCode {
    OK(0)				,	// Success
    ERROR(1)			,	// Error in communication
    COLLISION(2)		,	// Collission detected
    TIMEOUT(3)			,	// Timeout in communication.
    NO_ROOM(4)			,	// A buffer is not big enough.
    INTERNAL_ERROR(5)	,	// Internal error in the code. Should not happen ;-)
    INVALID(6)			,	// Invalid argument.
    CRC_WRONG(7)		,	// The CRC_A does not match
    MIFARE_NACK(0xff)	;	// A MIFARE PICC responded with NAK.

    private byte code;

    private StatusCode(int code) {
        this((byte) code);
    }

    private StatusCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
