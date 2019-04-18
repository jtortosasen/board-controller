package serial

import com.fazecast.jSerialComm.SerialPort
import extensions.trim
import kotlinx.coroutines.delay
import java.io.InputStream
import java.io.OutputStream

class SerialIO: ISerialIO {

    private lateinit var serialPort: SerialPort
    private lateinit var output: OutputStream
    private lateinit var input: InputStream

    private var mode9Bit: Boolean = false

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

    override suspend fun read(): ByteArray {

//        val byteArray = ArrayList<Byte>()
//        try {
//            var count = 0
//            while(true) {
//                val trunk = input.read()
//                if(trunk != 1){
//                    byteArray.add(trunk.toByte())
//                    continue
//                }
//                else if(count >= 10){
//                    return byteArray.toByteArray()
//                }
//            }
//        }catch (e: Throwable){ }
//
//        return byteArray.toByteArray()
        val buffer = ByteArray(255)
        serialPort.readBytes(buffer, buffer.size.toLong())
        return buffer.trim()
    }

//    fun InputStream.readAndPrintSerial(){
//        try {
//            var count = 0
//            while(true) {
//                Thread.sleep(10)
//                val trunk = read()
//                if(trunk == 1)
//                    count++
//                if(count >= 10)
//                    break
//                if(trunk != 1)
//                    count = 0
//                print(trunk.toChar())
//            }
//            println()
//        }catch (e: Throwable){
//            println()
//        }
//    }


}