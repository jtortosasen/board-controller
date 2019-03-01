import com.serialpundit.serial.SerialComManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import com.serialpundit.serial.SerialComManager.SMODE
import com.serialpundit.serial.SerialComInByteStream
import com.serialpundit.serial.SerialComOutByteStream
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun main(args: Array<String>) = runBlocking {

    while (true){
        //    rutinaFranco()
        rutinaSas()
        delay(15000L)
    }
}

fun rutinaFranco() = GlobalScope.launch  {

    val serialManager = SerialComManager()
    val handle = serialManager.openComPort("/dev/ttyAMA4",true, true, false)
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_NONE, SerialComManager.BAUDRATE.B9600, 0);
    serialManager.configureComPortControl(handle, SerialComManager.FLOWCONTROL.NONE, 'x', 'x', false, false);
    val output = serialManager.getIOStreamInstance(SerialComManager.OutputStream, handle, SMODE.NONBLOCKING) as SerialComOutByteStream
    val input = serialManager.getIOStreamInstance(SerialComManager.InputStream, handle, SMODE.NONBLOCKING) as SerialComInByteStream

    println(serialManager.getPortName(handle))

    val TOTBytes = byteArrayOf(0x54, 0x4F, 0x54, 0x0D, 0x0A)
    val REGBytes = byteArrayOf(0x72, 0x65, 0x67, 0x0D, 0x0A)

    output.write(REGBytes)
    while (true){
        val read = input.read()
        if(read < 0)
            break
        print(read.toChar())
    }
    output.write(TOTBytes)
    while (true){
        val read = input.read()
        if(read < 0)
            break
        print(read.toChar())
    }
}

fun rutinaSas() = GlobalScope.launch {
    val serialManager = SerialComManager()
    val handle = serialManager.openComPort("/dev/ttyAMA4",true, true, false)
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_NONE, SerialComManager.BAUDRATE.B9600, 0);
    serialManager.configureComPortControl(handle, SerialComManager.FLOWCONTROL.NONE, 'x', 'x', false, false);
    val output = serialManager.getIOStreamInstance(SerialComManager.OutputStream, handle, SMODE.NONBLOCKING) as SerialComOutByteStream
    val input = serialManager.getIOStreamInstance(SerialComManager.InputStream, handle, SMODE.NONBLOCKING) as SerialComInByteStream

    println("escribiendo 0x80")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_EVEN, SerialComManager.BAUDRATE.B19200, 0);
    output.write(byteArrayOf(0x80.toByte()))
    delay(20)
    println("escribiendo 0x81")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_ODD, SerialComManager.BAUDRATE.B19200, 0);
    output.write(byteArrayOf(0x81.toByte()))
//    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_NONE, SerialComManager.BAUDRATE.B19200, 0);

    delay(300)
    var count = 0
    while (true){
        val trunk = input.read()
        println(trunk)

        if(trunk < 0 && count == 10)
            break
        else
            count++
    }


    println("")
    println("escribiendo 0x01")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_EVEN, SerialComManager.BAUDRATE.B19200, 0);
    output.write(0x01)
    delay(20)
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_ODD, SerialComManager.BAUDRATE.B19200, 0);
    println("escribiendo 0x54")
    output.write(0x54)
//
    delay(300)
    count = 0
    while (true){
        val trunk = input.read()
        println(trunk)

        if(trunk < 0 && count == 20)
            break
        else if(trunk < 0)
            count++
    }
    println("")
    println("escribiendo 0x01")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_EVEN, SerialComManager.BAUDRATE.B19200, 0);
    output.write(0x01)
    delay(20)

    println("")
    println("escribiendo 0x0F")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_EVEN, SerialComManager.BAUDRATE.B19200, 0);
    output.write(0x0F)
    delay(20)

    println("")
    println("escribiendo 0x2F")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_ODD, SerialComManager.BAUDRATE.B19200, 0);
    output.write(0x2F)
    delay(20)

    println("")
    println("escribiendo 0xE1")
    serialManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_EVEN, SerialComManager.BAUDRATE.B19200, 0);
    output.write(0xE1)
    delay(20)

    count = 0
    while (true){
        val trunk = input.read()
        println(trunk)

        if(trunk < 0 && count == 20)
            break
        else if(trunk < 0)
            count++
    }
}

