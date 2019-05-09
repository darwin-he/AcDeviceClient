package device.RfidSensorModule.Mfrc522;

/**
 * @author darwin_he
 * @date 2019/4/22 0:53
 */
public enum PcdRegister {
    // Registers
    //Reserved00(0x00),
    COMMAND_REG(0x01),				// starts and stops command execution
    COM_INT_EN_REG(0x02),			// enable and disable interrupt request control bits
    DIV_INT_EN_REG(0x03),			// enable and disable interrupt request control bits
    COM_IRQ_REG(0x04),				// interrupt request bits
    DIV_IRQ_REG(0x05),				// interrupt request bits
    ERROR_REG(0x06),				// error bits showing the error status of the last command executed
    STATUS1_REG(0x07),				// communication status bits
    STATUS2_REG(0x08),				// receiver and transmitter status bits
    FIFO_DATA_REG(0x09),			// input and output of 64 byte FIFO buffer
    FIFO_LEVEL_REG(0x0A),			// number of bytes stored in the FIFO buffer
    WATER_LEVEL_REG(0x0B),			// level for FIFO underflow and overflow warning
    CONTROL_REG(0x0C),				// miscellaneous control registers
    BIT_FRAMING_REG(0x0D),			// adjustments for bit-oriented frames
    COLL_REG(0x0E),					// bit position of the first bit-collision detected on the RF interface
    //RESERVED_01(0x0F),
    //RESERVED_10(0x10),
    MODE_REG(0x11),					// defines general modes for transmitting and receiving
    TX_MODE_REG(0x12),				// defines transmission data rate and framing
    RX_MODE_REG(0x13),				// defines reception data rate and framing
    TX_CONTROL_REG(0x14),			// controls the logical behavior of the antenna driver pins TX1 and TX2
    TX_ASK_REG(0x15),				// controls the setting of the transmission modulation
    TX_SEL_REG(0x16),				// selects the internal sources for the antenna driver
    RX_SEL_REG(0x17),				// selects internal receiver settings
    RX_THRESHOLD_REG(0x18),			// selects thresholds for the bit decoder
    DEMOD_REG(0x19),				// defines demodulator settings
    //Reserved11(0x1A),
    //Reserved12(0x1B),
    MIFARE_TX_REG(0x1C),			// controls some MIFARE communication transmit parameters
    MIFARE_RX_REG(0x1D),			// controls some MIFARE communication receive parameters
    //Reserved14(0x1E),
    SERIAL_SPEED_REG(0x1F),			// selects the speed of the serial UART interface
    //Reserved20(0x20),
    CRC_RESULT_REG_MSB(0x21),		// shows the MSB values of the CRC calculation
    CRC_RESULT_REG_LSB(0x22),		// shows the LSB values of the CRC calculation
    //Reserved21(0x23),
    MOD_WIDTH_REG(0x24),			// controls the ModWidth setting
    //Reserved22(0x25),
    RF_CONFIG_REG(0x26),			// configures the receiver gain
    GS_N_REG(0x27),					// selects the conductance of the antenna driver pins TX1 and TX2 for modulation
    CWGsP_REG(0x28),				// defines the conductance of the p-driver output during periods of no modulation
    ModGsP_REG(0x29),				// defines the conductance of the p-driver output during periods of modulation
    T_MODE_REG(0x2A),				// defines settings for the internal timer
    T_PRESCALER_REG(0x2B),			//
    T_RELOAD_REG_MSB(0x2C),			// defines the 16-bit timer reload value
    T_RELOAD_REG_LSB(0x2D),
    T_COUNTER_VALUE_REG_MSB(0x2E),	// shows the 16-bit timer value
    T_COUNTER_VALUE_REG_LSB(0x2F),
    //Reserved30(0x30),
    TEST_SEL1_REG(0x31),			// general test signal configuration
    TEST_SEL2_REG(0x32),			// general test signal configuration and PRBS control
    TEST_PIN_EN_REG(0x33),			// enables pin output driver on pins D1 to D7
    TEST_PIN_VALUE_REG(0x34),		// defines the values for D1 to D7 when it is used as an I/O bus
    TEST_BUS_REG(0x35),				// shows the status of the internal test bus
    AUTO_TEST_REG(0x36),			// controls the digital self test
    VERSION_REG(0x37),				// shows the software version
    ANALOG_TEST_REG(0x38),			// controls the pins AUX1 and AUX2
    TEST_DAC1_REG(0x39),			// defines the test value for TestDAC1
    TEST_DAC2_REG(0x3A),			// defines the test value for TestDAC2
    TEST_ADC_REG(0x3B);				// shows the value of ADC I and Q channels
    //Reserved31(0x3C),
    //Reserved32(0x3D),
    //Reserved33(0x3E),
    //Reserved34(0x3F);

    private byte value;
    private byte address;

    private PcdRegister(int value) {
        this.value = (byte) value;
        address = (byte) ((value << 1) & 0x7e);
    }

    public byte getAddress() {
        return address;
    }

    public byte getValue() {
        return value;
    }
}
