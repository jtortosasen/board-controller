package serial

import command.Command
import config.IConfiguration
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import tcp.input.IHandler
import tcp.output.ISender

class SerialManager(handle: IHandler, sender: ISender, private val serialIO: ISerialIO, config: IConfiguration) :
    ISerialManager {


    private val logger = KotlinLogging.logger {  }

    private val handleChannel = Channel<Command.IO>()
    private val senderChannel = Channel<ByteArray>()

    private lateinit var state: Command.IO
    private lateinit var masterJob: Job


    init {
        handle.channel(channel = handleChannel)
        sender.channel(channel = senderChannel)
        serialIO.comPort(config.serialPort)
    }

    private val masterMode: Boolean
        get() {
            if(this::state.isInitialized)
                if(state is Command.IO.MasterMode)
                    return true
            return false
        }


    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            val command = handleChannel.receive()
            routeCommand(command)
        }
    }

    private suspend fun listenSerial() = CoroutineScope(Dispatchers.IO).launch {
        logger.debug { "Listening serial" }
        while (isActive) {
            val data = serialIO.read()
            logger.debug { "Recibido de maquina" }
            data.forEach { print("${it.toString(16)}-") }
            println()
            val header = byteArrayOf(0x55, 0xFF.toByte(), 0x45, 0xFF.toByte(), 0x45, 0x03)
            val dataWithHeader = applyHeader(header, data)
            senderChannel.send(dataWithHeader)
        }
    }

    private fun applyHeader(first: ByteArray, second: ByteArray): ByteArray{
        val arrayWithHeader = ByteArray(first.size + second.size)
        for((index,byte) in first.withIndex()){
            arrayWithHeader[index] = byte
        }
        for((index,byte) in second.withIndex()){
            arrayWithHeader[index+first.size] = byte
        }
        return arrayWithHeader
    }

    private suspend fun routeCommand(command: Command.IO) = when (command) {
        is Command.IO.OpenSlave9600B8N1  -> openPort(command)
        is Command.IO.OpenSlave19200B8N1 -> openPort(command)
        is Command.IO.OpenSlave19200B9N1 -> openPort(command)
        is Command.IO.CloseSlave         -> closePort(command)
        is Command.IO.SendSlave          -> sendData(command)
        is Command.IO.MasterMode         -> startMasterMode(command)
    }

    private suspend fun openPort(command: Command.IO) {
        logger.debug { "Opening Port" }
        setCommand(command)
        setPortParams(command)
        senderChannel.send(byteArrayOf(0x06))
        listenSerial()
    }

    private fun configurePort(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int) {
        serialIO.serialParams(
            baudRate = baudRate,
            dataBits = dataBits,
            parity = parity,
            stopBits = stopBits
        )
    }

    private suspend fun setCommand(command: Command.IO) {
        if (masterMode) {
            logger.debug { "Stopping master mode" }
            state = command
            masterJob.cancelAndJoin()
        } else
            logger.debug { "Setting command (no previous master mode)" }
            state = command
    }

    private fun setPortParams(command: Command.IO) {
        logger.debug { "Setting serial params" }
        configurePort(command.baudRate, command.dataBits, command.parity, command.stopBits)
    }

    private suspend fun closePort(command: Command.IO.CloseSlave) {
        setCommand(command)
        setPortParams(command)
    }

    private suspend fun sendData(command: Command.IO.SendSlave) {
        setCommand(command)
        write(command.content)
    }

    private fun write(byteArray: ByteArray) {
        serialIO.write(byteArray)
    }

    private suspend fun startMasterMode(command: Command.IO.MasterMode) {
        setCommand(command)
        withContext(Dispatchers.IO){
            when (command) {
                is Command.IO.MasterMode.Sas -> masterJob = launch { sasRoutine() }
            }
        }
    }

    private suspend fun sasRoutine(){

        serialIO.flush()
        serialIO.mode9Bit = true
        serialIO.write(byteArrayOf(0x80.toByte(), 0x81.toByte()))
        val array = serialIO.read()
        serialIO.write(byteArrayOf(0x01.toByte(), 0x54.toByte()))
        serialIO.write(
            byteArrayOf(
                0x01.toByte(), 0x6f.toByte(), 0x12.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x00.toByte(), 0x03.toByte(), 0x00.toByte(), 0x04.toByte(),
                0x00.toByte(), 0x08.toByte(), 0x00.toByte(), 0x09.toByte(),
                0x00.toByte(), 0x0b.toByte(), 0x00.toByte(), 0x6e.toByte(),
                0x00.toByte(), 0x0A.toByte(), 0x88.toByte()
            ))
    }



//    fun sasRoutine() {
//
//        val waitTime: Long = 20
//        val comPort = SerialPort.getCommPorts()[1]
//        println(comPort.descriptivePortName)
//        if (comPort.openPort()) {

//
//            val input: InputStream = comPort.inputStream
//            val output: OutputStream = comPort.outputStream
//            comPort.baudRate = 19200
//            comPort.parity = SerialPort.MARK_PARITY
//            comPort.numDataBits = 8
//            comPort.numStopBits = SerialPort.ONE_STOP_BIT
//            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
//
//            while (true) {
//                var maxAttempts = 0
//                println("enviando 80 81 intento $maxAttempts")
//                do {
//                    if (maxAttempts >= 7)
//                        break
//                    comPort.parity = SerialPort.NO_PARITY
//                    output.write(sequence0x80)
//                    output.write(sequence0x81)
//                    comPort.parity = SerialPort.NO_PARITY
//                    val value = input.read()
//                    println(value.toUByte())
//                    maxAttempts++
//                } while (value == 0x70)
//                if (maxAttempts < 7) {
//                    println("enviando 01")
//                    comPort.parity = SerialPort.NO_PARITY
//                    output.write(sequence0x01)
//                    comPort.parity = SerialPort.NO_PARITY
//                    println("enviando 54")
//                    output.write(sequence0x54)
//                    comPort.parity = SerialPort.NO_PARITY
//                    val temp = mutableListOf<Byte>()
//                    println("leyendo")
//                    while (true) {
//                        val temp1 = input.read()
//                        println(temp1)
//                        if (temp1 < 0)
//                            break
//                        temp.add(temp1.toByte())
//                    }
//                    temp.let {
//                        if (temp.size > 2) {
//                            comPort.parity = SerialPort.MARK_PARITY
//                            output.write(sequence0x01)
//                            delay(waitTime)
//                            comPort.parity = SerialPort.SPACE_PARITY
//                            output.write(sequence0x0F2FE1)
//                            comPort.parity = SerialPort.NO_PARITY
//                            val temp2 = mutableListOf<Byte>()
//                            while (true) {
//                                val temp1 = input.read()
//                                if (temp1 < 0)
//                                    break
//                                temp2.add(temp1.toByte())
//                            }
//                            if (temp2.size > 2) {
//                                println(temp2.toByteArray().toString(Charset.defaultCharset()))
//                            }
//                        }
//                    }
//                } else {
//                    println("intento maximo superado")
//                }
//                delay(15000L)
//            }
//        }
//    }
}