package serial

import com.fazecast.jSerialComm.SerialPort
import java.io.InputStream
import java.io.OutputStream


interface ISerialIO {
    var mode9Bit: Boolean
    fun comPort(serialPortName: String)
    fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int)
    fun write(byteArray: ByteArray)
    suspend fun read(): ByteArray
    suspend fun flush()
}


class SerialIO: ISerialIO {

    private lateinit var serialPort: SerialPort
    private lateinit var output: OutputStream
    private lateinit var input: InputStream

    override var mode9Bit: Boolean = false
    private val even = SerialPort.EVEN_PARITY
    private val odd = SerialPort.ODD_PARITY
    private val parityArray = (0..255).map {
        if (Integer.bitCount(it) % 2 == 0) intArrayOf(odd, even) else intArrayOf(even, odd)
    }

    override fun comPort(serialPortName: String) {
        serialPort = SerialPort.getCommPort(serialPortName)
        serialPort.openPort()

        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)

        output = serialPort.outputStream
        input = serialPort.inputStream
    }

    override fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int){
        mode9Bit = dataBits == 9
        serialPort.baudRate = baudRate
        serialPort.numDataBits = dataBits
        serialPort.parity = parity
        serialPort.numStopBits = stopBits
    }

    override fun write(byteArray: ByteArray) {
        if(mode9Bit)
            output.write9bit(byteArray)
        else
            output.write(byteArray)
    }

    private fun OutputStream.write9bit(byteArray: ByteArray){
        byteArray.forEachIndexed { index, byte ->
            if(index == 0){
                val tempParity = parityArray[byte.toInt()][0]
                if (serialPort.parity != tempParity){
                    serialPort.parity = tempParity
                }
            }
            else{
                val tempParity = parityArray[byte.toInt()][1]

                if (serialPort.parity != tempParity){
                    serialPort.parity = tempParity
                }
            }
            write(byte.toInt())
        }
    }

    override suspend fun flush() {
        val bytesAvailable = serialPort.bytesAvailable()

        if (bytesAvailable > 0) {
            val chunkBuffer = ByteArray(bytesAvailable)
            serialPort.readBytes(chunkBuffer, chunkBuffer.size.toLong())
        }
    }

    override suspend fun read(): ByteArray {
        val buffer = ArrayList<Byte>()
        var readFlag = false
        var startTime: Long = 0

        while(true){
//            delay(1000)
            val bytesAvailable = serialPort.bytesAvailable()

            if(bytesAvailable > 0){
                val chunkBuffer = ByteArray(bytesAvailable)

                if(serialPort.readBytes(chunkBuffer, chunkBuffer.size.toLong()) <= 0)
                    continue
                startTime = System.currentTimeMillis()
                readFlag = true
                for (chunk in chunkBuffer)
                    buffer.add(chunk)
            }else{
                if (!readFlag)
                    continue
                if((System.currentTimeMillis() - startTime) < 100 || buffer.size <= 0 )
                    continue
                return buffer.toByteArray()
            }
        }
    }
}