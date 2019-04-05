package serial

interface ISerialIO {
    fun serialPort(serialPortName: String)
    fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int)
    fun write(byteArray: ByteArray)
    fun read(): ByteArray

}