package components.rfid_reader.mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 0:57
 */
public   enum PiccCommand {
    // The commands used by the PCD to manage communication with several PICCs (ISO 14443-3, Type A, section 6.4)
    REQUEST_A(0x26),		// REQuest command, Type A. Invites PICCs in state IDLE to go to READY and prepare for anticollision or selection. 7 bit frame.
    MF_READ(0x30),			// Reads one 16 byte block from the authenticated sector of the PICC. Also used for MIFARE Ultralight.
    HALT_A(0x50),			// HaLT command, Type A. Instructs an ACTIVE PICC to go to state HALT.
    WAKE_UP_A(0x52),		// Wake-UP command, Type A. Invites PICCs in state IDLE and HALT to go to READY(*) and prepare for anticollision or selection. 7 bit frame.
    MF_AUTH_KEY_A(0x60),	// Perform authentication with key A
    MF_AUTH_KEY_B(0x61),	// Perform authentication with key B
    CASCADE_TAG(0x88),		// Cascade Tag. Not really a command, but used during anti collision.
    SEL_CL1(0x93),			// Anti collision/Select, Cascade Level 1
    SEL_CL2(0x95),			// Anti collision/Select, Cascade Level 2
    SEL_CL3(0x97),			// Anti collision/Select, Cascade Level 3
    MF_WRITE(0xA0),			// Writes one 16 byte block to the authenticated sector of the PICC. Called "COMPATIBILITY WRITE" for MIFARE Ultralight.
    UL_WRITE(0xA2),			// Writes one 4 byte page to the PICC.
    MF_TRANSFER(0xB0),		// Writes the contents of the internal data register to a block.
    MF_DECREMENT(0xC0),		// Decrements the contents of a block and stores the result in the internal data register.
    MF_INCREMENT(0xC1),		// Increments the contents of a block and stores the result in the internal data register.
    MF_RESTORE(0xC2);		// Reads the contents of a block into the internal data register.

    private byte value;

    private PiccCommand(int value) {
        this((byte) value);
    }

    private PiccCommand(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
