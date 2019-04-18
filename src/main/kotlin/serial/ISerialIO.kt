package serial

interface ISerialIO {
    fun comPort(serialPortName: String)
    fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int)
    fun write(byteArray: ByteArray)
    suspend fun read(): ByteArray

}