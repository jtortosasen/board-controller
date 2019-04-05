package serial

import com.fazecast.jSerialComm.SerialPort
import java.io.InputStream
import java.io.OutputStream

class SerialIO: ISerialIO {

    private lateinit var serialPort: SerialPort
    private lateinit var output: OutputStream
    private lateinit var input: InputStream

    private var baudRate: Int = 0
    private var dataBits: Int = 0
        set(value) =
            if(value == 9){
                mode9Bit = true
                field = 8
            }else
                field = value
    private var parity : Int = 0
    private var stopBits : Int = 0

    private var mode9Bit: Boolean = false

    private val even = SerialPort.EVEN_PARITY
    private val odd = SerialPort.ODD_PARITY

    private val parityArray = (0..255).map {
        if (Integer.bitCount(it) % 2 == 0) intArrayOf(odd, even) else intArrayOf(even, odd)
    }

    override fun serialPort(serialPortName: String) {
        serialPort = SerialPort.getCommPort(serialPortName)
        output = serialPort.outputStream
        input = serialPort.inputStream
    }

    override fun serialParams(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int){
        this.baudRate = baudRate
        this.dataBits = dataBits
        this.parity = parity
        this.stopBits = stopBits
        applyParams()
    }

    private fun applyParams() = serialPort.apply {
            this.numDataBits = dataBits
            this.baudRate = baudRate
            this.parity = parity
            this.numStopBits = stopBits
    }

    override fun write(byteArray: ByteArray) {
        if(mode9Bit)
            write9bit(byteArray)
        else{

        }
    }

    private fun write9bit(byteArray: ByteArray){

    }

    override fun read(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}