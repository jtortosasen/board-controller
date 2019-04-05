package serial

import config.IConfiguration
import tcp.input.Command
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.channels.Channel
import tcp.input.IHandler
import tcp.output.ISender

class SerialManager(handle: IHandler, sender: ISender, private val serialIO: ISerialIO, config: IConfiguration): ISerialManager {


    private val scope = CoroutineScope(Dispatchers.IO)
    private val handleChannel = Channel<Command.IO>()
    private val senderChannel = Channel<ByteArray>()

    private lateinit var state: Command.IO
    private lateinit var masterJob: Job

    private val masterMode = when (state){
        is Command.IO.CirsaMode -> true
        is Command.IO.OpenSlave9600B8N1 -> false
        is Command.IO.OpenSlave19200B8N1 -> false
        is Command.IO.OpenSlave19200B9N1 -> false
        is Command.IO.CloseSlave -> false
        is Command.IO.SendSlave -> false
    }


    init {
        handle.channel(channel = handleChannel)
        sender.channel(channel = senderChannel)
        serialIO.serialPort(config.serialPort)
    }

    override fun start() = scope.launch {
        listenSerial()
        listenChannel()
    }

    private fun listenSerial() = scope.launch {
        while(isActive){
            val data = serialIO.read()
            senderChannel.send(data)
        }
    }

    private fun listenChannel() = scope.launch {
        while(isActive){
            val command = handleChannel.receive()
            routeCommand(command)
        }
    }

    private suspend fun routeCommand(command: Command.IO) = when(command) {
        is Command.IO.OpenSlave9600B8N1  -> openPort(command)
        is Command.IO.OpenSlave19200B8N1 -> openPort(command)
        is Command.IO.OpenSlave19200B9N1 -> openPort(command)
        is Command.IO.CloseSlave         -> closePort(command)
        is Command.IO.SendSlave          -> sendData(command)
        is Command.IO.CirsaMode          -> masterMode(command)
    }

    private suspend fun openPort(command: Command.IO){
        setCommand(command)
        setPortParams(command)
    }

    private fun configurePort(baudRate: Int, dataBits: Int, parity: Int, stopBits: Int){
        serialIO.serialParams(
            baudRate = baudRate,
            dataBits = dataBits,
            parity = parity,
            stopBits = stopBits
        )
    }

    private suspend fun setCommand(command: Command.IO){
        if(masterMode){
            state = command
            masterJob.cancelAndJoin()
        }else
            state = command
    }

    private fun setPortParams(command: Command.IO){
        command.apply {
            configurePort(baudRate, dataBits, parity, stopBits)
        }
    }

    suspend fun closePort(command: Command.IO.CloseSlave) {
        setCommand(command)
        setPortParams(command)
    }

    suspend fun sendData(command: Command.IO.SendSlave) {
        setCommand(command)
        write(command.content)
    }

    private suspend fun write(byteArray: ByteArray){
        serialIO.write(byteArray)
    }

    suspend fun masterMode(command: Command.IO) {
        setCommand(command)
        when(command){
            is Command.IO.CirsaMode -> masterJob = cirsaRoutine()
        }
    }

    private fun cirsaRoutine() = scope.launch {

    }

}