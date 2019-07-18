package serial

import command.Command
import command.extractCommand
import config.IConfiguration
import gpio.LedManager
import gpio.LedState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import tcp.input.IHandler
import tcp.output.ISender
import java.lang.Exception
import java.util.*


interface ISerialManager {
    suspend fun start(): Job
    fun close(): Boolean
    var led: LedState
}

@kotlin.ExperimentalUnsignedTypes
class SerialManager(handle: IHandler, sender: ISender, val config: IConfiguration) :
    ISerialManager {

    private val handleChannel = Channel<Command.IO>(Channel.UNLIMITED)
    private val senderChannel = Channel<ByteArray>(Channel.UNLIMITED)

    init {
        handle.channel(channel = handleChannel)
        sender.channel(channel = senderChannel)
    }

    private val logger = KotlinLogging.logger { }

    private val serialIO : SerialIO = SerialIO()

    private lateinit var state: Command.IO
    private lateinit var masterJob: Job
    private var listenerSerialJob: Job? = null
    private var isCheckingStatus = false
    @Volatile var checkedStatus: Int = 0
    private val isMasterActivated: Boolean
        get() {
            if (this::state.isInitialized)
                if (state is Command.IO.MasterMode)
                    return true
            return false
        }

    override lateinit var led: LedState


    override fun close(): Boolean {
        return serialIO.close()
    }

    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        serialIO.comPort(config.serialPort)
        serialIO.led = led
        serialIO.close()
        while (isActive) {
            val command = handleChannel.receive()
            routeCommand(command)
        }
        logger.debug { "Clossing serial port" }
        serialIO.close()
    }

    private suspend fun listenSerial() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            try{
                if(isCheckingStatus){
                    val data = serialIO.read(timeout=3000)
                    logger.debug { "Checking status serial RAW:" }
                    logger.debug { data.map { it.toUByte().toString(16) } }
                    if(data.isEmpty())
                        logger.debug { "EMPTY" }
                    else logger.debug { "NOT EMPTY" }
                    checkedStatus =
                        if(data.isEmpty()) 0
                        else 1
                }else{
                    val data = serialIO.read()
                    if(data.isNotEmpty()){
                        logger.debug { "Recieved from serial RAW:" }
                        logger.debug { data.map { it.toUByte().toString(16) } }
                        send(data)
                    }
                    led.color = LedManager.LedColors.LightBlue
                }
            }catch (e: Exception){
                logger.error(e) { e }
            }
        }
        serialIO.close()
    }

    private fun applyHeader(first: ByteArray, second: ByteArray): ByteArray {
        val arrayWithHeader = ByteArray(first.size + second.size)
        for ((index, byte) in first.withIndex()) {
            arrayWithHeader[index] = byte
        }
        for ((index, byte) in second.withIndex()) {
            arrayWithHeader[index + first.size] = byte
        }
        return arrayWithHeader
    }

    private suspend fun routeCommand(command: Command.IO) = when (command) {
        is Command.IO.OpenSlave9600B8N1  -> openPortAsSlave(command)
        is Command.IO.OpenSlave19200B8N1 -> openPortAsSlave(command)
        is Command.IO.OpenSlave19200B9N1 -> openPortAsSlave(command)
        is Command.IO.CloseSlave         -> closePort(command)
        is Command.IO.SendSlave          -> when(command){
            is Command.IO.CheckCommunication -> checkCommunication(command)
            else -> writeData(command)
        }
        is Command.IO.MasterMode         -> startMasterMode(command)
    }

    private suspend fun checkCommunication(command: Command.IO.CheckCommunication){
        logger.debug { "Checking status" }
        serialIO.close()
        delay(1000)
        logger.debug { "closing COM port" }
        isCheckingStatus = true
        delay(1000)
        serialIO.open()
        logger.debug { "open COM port" }
        when(val subCommand = byteArrayOf(command.content[0]).extractCommand()){
            is Command.IO.OpenSlave9600B8N1  -> {
                logger.debug { "Write to com port " }
                configureComPort(subCommand)
                write(Arrays.copyOfRange(command.content, 1, command.content.size))
            }
            is Command.IO.OpenSlave19200B8N1 -> {
                logger.debug { "Write to com port " }
                configureComPort(subCommand)
                write(Arrays.copyOfRange(command.content, 1, command.content.size))
            }
            is Command.IO.OpenSlave19200B9N1 -> {
                logger.debug { "Write to com port " }
                configureComPort(subCommand)
                write(byteArrayOf(0x80.toByte()))
                write(byteArrayOf(0x81.toByte()))
            }
        }
        delay(3000)
        isCheckingStatus = false
        if(checkedStatus == 1){
            send(byteArrayOf(0x06),command=0x46)
        }else{
            send(byteArrayOf(0x15), command=0x46)
        }

    }

    private suspend fun openPortAsSlave(command: Command.IO) {
        registerCommand(command)
        configureComPort(command)
        if(serialIO.open()){
            senderChannel.send(byteArrayOf(0x06))
        }else{
            senderChannel.send(byteArrayOf(0x15))
        }
        if(listenerSerialJob == null){
            listenerSerialJob = listenSerial()
        }else{
            listenerSerialJob?.let {
                if(!it.isActive){
                    logger.debug { "listenerSerialJob not active, initializating" }
                    listenerSerialJob = listenSerial()
                }
            }
        }
    }

    private fun configurePort(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int) {
        serialIO.serialParams(
            baudRate = baudRate,
            dataBits = dataBits,
            parity = parity,
            stopBits = stopBits
        )
    }

    private suspend fun registerCommand(command: Command.IO) {
        if (isMasterActivated) {
            logger.debug { "Stopping master mode" }
            state = command
            masterJob.cancelAndJoin()
        } else{
            listenerSerialJob?.let{
                if(command is Command.IO.MasterMode && it.isActive){
                    it.cancel()
                }
            }
            logger.debug { "Setting command (no previous master mode)" }
        }
        state = command
    }

    private fun configureComPort(command: Command.IO) {
        configurePort(command.baudRate, command.dataBits, command.parity, command.stopBits)
    }

    private suspend fun closePort(command: Command.IO.CloseSlave) {
        serialIO.close()
        registerCommand(command)
//        configureComPort(command)
    }

    private suspend fun writeData(command: Command.IO.SendSlave) {
        registerCommand(command)
        logger.debug { "Writing command content to serial com" }
        logger.debug {
            command.content.map { it.toUByte().toString(16) }
        }
        write(command.content)
    }

    private fun write(byteArray: ByteArray) {
        try{
            serialIO.write(byteArray)
        }catch (e: Exception){
            logger.error(e) { e }
        }
    }

    private suspend fun startMasterMode(command: Command.IO.MasterMode) {
        registerCommand(command)
        withContext(Dispatchers.IO) {
            when (command) {
                is Command.IO.MasterMode.Sas -> masterJob = launch { sasRoutine() }
            }
        }
    }

    private suspend fun sasRoutine() {
        withContext(Dispatchers.IO){
            serialIO.mode9Bit = true
            while (isActive){
                try{
                    serialIO.write(byteArrayOf(0x80.toByte(), 0x81.toByte()))
                    serialIO.read()
                    serialIO.write(byteArrayOf(0x01.toByte(), 0x54.toByte()))
                    serialIO.read()
                    serialIO.write(
                        byteArrayOf(
                            0x01.toByte(), 0x6f.toByte(), 0x12.toByte(), 0x00.toByte(),
                            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                            0x00.toByte(), 0x03.toByte(), 0x00.toByte(), 0x04.toByte(),
                            0x00.toByte(), 0x08.toByte(), 0x00.toByte(), 0x09.toByte(),
                            0x00.toByte(), 0x0b.toByte(), 0x00.toByte(), 0x6e.toByte(),
                            0x00.toByte(), 0x0A.toByte(), 0x88.toByte()
                        )
                    )
                    send(serialIO.read(), command = 0x93.toByte())
                    led.color = LedManager.LedColors.LightBlue
                }catch (e: Exception){
                    logger.error(e) { e }
                }
                delay(10000)
            }
            serialIO.mode9Bit = false
        }
    }

    private suspend fun send(data: ByteArray, command: Byte = 0x45){

        val headerSlave = byteArrayOf(0x55, 0xFF.toByte(), command, 0xFF.toByte(), command, 0x03)
        val headerMaster = byteArrayOf(0x55, 0xE0.toByte(), command, 0xE0.toByte(), command, 0x03)
        val dataWithHeader: ByteArray
        if(isMasterActivated)
            dataWithHeader = applyHeader(headerMaster, data)
        else
            dataWithHeader = applyHeader(headerSlave, data)
        senderChannel.send(dataWithHeader)
    }


//    fun sasRoutine() {
//
//        val waitTime: Long = 20
//        val comPort = SerialPort.getCommPorts()[1]
//        println(comPort.descriptivePortName)
//        if (comPort.openPortAsSlave()) {

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