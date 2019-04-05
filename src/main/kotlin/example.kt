//import com.fazecast.jSerialComm.SerialPort
//import kotlinx.coroutines.*
//
//
//import java.io.InputStream
//
//val even = SerialPort.EVEN_PARITY
//val odd = SerialPort.ODD_PARITY
//
//val mark = 0
//val space = 1
//
//
//val parityArray = (0..255).map { generatePair(it) }
//
//fun generatePair(n: Int) : IntArray {
//    return if (Integer.bitCount(n) % 2 == 0) intArrayOf(odd, even) else intArrayOf(even, odd)
//}
//
//
//fun main() = runBlocking {
//    parityArray.forEachIndexed { index, ints ->
//        if(index  % 4 == 0 && index != 0) println()
//        print("${ints.asList()}")
//    }
////    GlobalScope.launch(Dispatchers.IO){
////        println("Iniciandov...")
////        println()
//////        sasRoutine()
////        francoRoutine()
////    }.join()
//}
//
//fun InputStream.readAndPrintSerial(){
//    try {
//        var count = 0
//        while(true) {
//            Thread.sleep(10)
//            val trunk = read()
//            if(trunk == 1)
//                count++
//            if(count >= 10)
//                break
//            if(trunk != 1)
//                count = 0
//            print(trunk.toChar())
//        }
//        println()
//    }catch (e: Throwable){
//        println()
//    }
//}
//
//fun SerialPort.swapMarkSpace(bit: Int, data: Int){
//    if(bit !in 0..2)
//        throw Exception("bit only accepts 0 and 1 values")
//    if(data !in 0..255)
//        throw Exception("data only accepts 0 up to 255")
//
//    if(parityArray[data][bit] != parity)
//        parity = parityArray[data][bit]
//}
//
//suspend fun sasRoutine()  {
//
//    val comPort = SerialPort.getCommPorts()[0]
//
//    comPort.openPort()
//    comPort.baudRate = 19200
//    comPort.parity = SerialPort.NO_PARITY
//    comPort.numDataBits = 8
//    comPort.numStopBits = SerialPort.ONE_STOP_BIT
//
//    comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)
//    val input = comPort.inputStream
//    val output = comPort.outputStream
//
//
//    println("escribiendo 0x80")
//    comPort.swapMarkSpace(mark, 0x80)
//    output.write(0x80)
//
//    println("escribiendo 0x81")
//    comPort.swapMarkSpace(space, 0x80)
//    output.write(0x81)
//
//    delay(1000)
//    input.readAndPrintSerial()
//
//    println("escribiendo 0x01")
//    comPort.swapMarkSpace(mark, 0x01)
//    output.write(0x01)
//
//    println("escribiendo 0x54")
//    comPort.swapMarkSpace(space, 0x54)
//    output.write(0x54)
//
//    delay(1000)
//    input.readAndPrintSerial()
//
//    println("escribiendo 0x01")
//    comPort.swapMarkSpace(mark, 0x01)
//    output.write(0x01)
//
//    println("escribiendo 0x6f")
//    comPort.swapMarkSpace(space, 0x6f)
//    output.write(0x6f)
//
//    println("escribiendo 0x12")
//    comPort.swapMarkSpace(space, 0x12)
//    output.write(0x12)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x01")
//    comPort.swapMarkSpace(space, 0x01)
//    output.write(0x01)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x03")
//    comPort.swapMarkSpace(space, 0x03)
//    output.write(0x03)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x04")
//    comPort.swapMarkSpace(space, 0x04)
//    output.write(0x04)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x08")
//    comPort.swapMarkSpace(space, 0x08)
//    output.write(0x08)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x09")
//    comPort.swapMarkSpace(space, 0x09)
//    output.write(0x09)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x0b")
//    comPort.swapMarkSpace(space, 0x0b)
//    output.write(0x0b)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x6e")
//    comPort.swapMarkSpace(space, 0x6e)
//    output.write(0x6e)
//
//    println("escribiendo 0x00")
//    comPort.swapMarkSpace(space, 0x00)
//    output.write(0x00)
//
//    println("escribiendo 0x0A")
//    comPort.swapMarkSpace(space, 0x0A)
//    output.write(0x0A)
//
//    println("escribiendo 0x88")
//    comPort.swapMarkSpace(space, 0x88)
//    output.write(0x88)
//
//    delay(1000)
//    input.readAndPrintSerial()
//}
//
//
//suspend fun francoRoutine(){
//
//    println("Entrando en franco routine")
//    val comPort = SerialPort.getCommPort("ttyAMA4")
//
//    comPort.openPort()
//    comPort.baudRate = 9600
//    comPort.parity = SerialPort.NO_PARITY
//    comPort.numDataBits = 8
//    comPort.numStopBits = SerialPort.ONE_STOP_BIT
//
//    comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)
//    val input = comPort.inputStream
//    val output = comPort.outputStream
//    val TOTBytes = byteArrayOf(0x54, 0x4F, 0x54, 0x0D, 0x0A)
//    val REGBytes = byteArrayOf(0x72, 0x65, 0x67, 0x0D, 0x0A)
//
//    output.write(REGBytes)
//    delay(500)
//    input.readAndPrintSerial()
//
//    output.write(TOTBytes)
//    delay(500)
//    input.readAndPrintSerial()
//}
