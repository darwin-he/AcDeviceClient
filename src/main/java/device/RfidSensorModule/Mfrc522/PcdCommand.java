package device.RfidSensorModule.Mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 0:55
 */
public enum PcdCommand {
    IDLE(0b0000),				// no action, cancels current command execution
    MEM(0b0001),				// stores 25 bytes into the internal buffer
    GENERATE_RANDOM_ID(0b0010),	// generates a 10-byte random ID number
    CALC_CRC(0b0011),			// activates the CRC coprocessor or performs a self test
    TRANSMIT(0b0100),			// transmits data from the FIFO buffer
    NO_CMD_CHANGE(0b0111),		// no command change, can be used to modify the
    // CommandReg register bits without affecting the command,
    // for example, the PowerDown bit
    RECEIVE(0b1000),			// activates the receiver circuits
    TRANSCEIVE(0b1100),			// transmits data from FIFO buffer to antenna and automatically
    // activates the receiver after transmission
    MF_AUTHENT(0b1110),			// performs the MIFARE standard authentication as a reader
    SOFT_RESET(0b1111);			// resets the MFRC522

    private byte value;

    private PcdCommand(byte value) {
        this.value = value;
    }

    private PcdCommand(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
