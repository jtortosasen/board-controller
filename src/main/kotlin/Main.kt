import com.fazecast.jSerialComm.SerialPort





fun main() {

    val comPort = SerialPort.getCommPorts()[0]

    comPort.openPort()
    comPort.setComPortParameters()
//    comPort.apply {
//        println(baudRate)
//        baudRate = 19200
//        println(baudRate)
//        parity = SerialPort.SPACE_PARITY
//        println(parity)
//
//    }
//
////    val a = 0x010F.toByte()
////    val b1 =0x01.toByte()
////    val b2 =0x0F.toByte()
////    val c: Int = b1.toInt() or b2.toInt().shl(8)
////    print(c)
////    val networkSocket = NetworkSocket(Configuration())
////    networkSocket.start()

}