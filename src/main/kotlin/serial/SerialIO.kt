package serial

import com.fazecast.jSerialComm.SerialPort
import gpio.LedManager
import gpio.LedState
import kotlinx.coroutines.delay
import java.io.InputStream
import java.io.OutputStream


interface ISerialIO {
    var mode9Bit: Boolean
    fun comPort(serialPortName: String)
    fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int)
    fun write(byteArray: ByteArray)
    suspend fun read(): ByteArray
    suspend fun flush()
    fun open()
    fun close(): Boolean
    var led: LedState
}

@kotlin.ExperimentalUnsignedTypes
class SerialIO: ISerialIO {

    private lateinit var serialPort: SerialPort
    private lateinit var output: OutputStream
    private lateinit var input: InputStream

    override var mode9Bit: Boolean = false

    private val even = "-parodd"
    private val odd = "parodd"
    private var currentParity = ""
    set(value) {
        bashCommand(command = "stty -F /dev/ttyAMA4 19200 cs8 -cstopb ignpar parenb $value")
        field = value
    }

    val parityArray = (0..255).map { generatePair(it) }
    override lateinit var led: LedState

    fun generatePair(n: Int) : Array<String> {
        return if (Integer.bitCount(n) % 2 == 0) arrayOf(odd, even) else arrayOf(even, odd)
    }

    private fun bashCommand(command: String){
        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", "-c", command)
        try{
            val process = processBuilder.start()
            process.waitFor()
        }catch (e: Exception){ }
    }

    override fun comPort(serialPortName: String) {
        serialPort = SerialPort.getCommPort(serialPortName)
    }

    override fun open(){
        serialPort.openPort()
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)
        output = serialPort.outputStream
        input = serialPort.inputStream
    }

    override fun close(): Boolean {
        return serialPort.closePort()
    }

    override fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int){
        mode9Bit = dataBits == 9
        serialPort.baudRate = baudRate
        serialPort.numDataBits = 8
        serialPort.parity = parity
        serialPort.numStopBits = stopBits
    }

    override fun write(byteArray: ByteArray) {
        try {
            if(mode9Bit)
                output.write9bit(byteArray)
            else
                output.write(byteArray)
        }catch (e: Exception) {}
    }

    private fun OutputStream.write9bit(byteArray: ByteArray){
        byteArray.forEachIndexed { index, byte ->
            if(index == 0){
                val tempParity = parityArray[byte.toUByte().toInt()][0]

                if (currentParity != tempParity){
                    currentParity = tempParity
                }
            }
            else{
                val tempParity = parityArray[byte.toUByte().toInt()][1]

                if (currentParity != tempParity){
                    currentParity = tempParity
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

            delay(500)

            val bytesAvailable = serialPort.bytesAvailable()

            if(bytesAvailable > 0){
                led.color = LedManager.LedColors.Green

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
                led.color = LedManager.LedColors.LightBlue
                return buffer.toByteArray()
            }
        }
    }
}