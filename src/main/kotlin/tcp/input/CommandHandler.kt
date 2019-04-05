package tcp.input

import extensions.extractCommand
import kotlinx.coroutines.channels.SendChannel


class CommandHandler: IHandler {

    private lateinit var channel: SendChannel<Command.IO>
    override fun channel(channel: SendChannel<Command.IO>) {
        this.channel = channel
    }

    override suspend fun handle(data: ByteArray) {
        handleCommand(data.extractCommand())
    }

    private suspend fun handleCommand(command: Command) = when (command) {
    is Command.IO          -> handleIO(command)
    is Command.IdMacNACK   -> IdNack()
    is Command.OpenLed     -> openLed()
    is Command.Restart     -> restart()
    is Command.Update      -> update()
    is Command.PlayVideo   -> playVideo()
    is Command.UpdateVideo -> updateVideo()
    is Command.IdMacACK    -> {}
    is Command.None        -> {}
    }

    override suspend fun handleIO(command: Command.IO) {
        channel.send(command)
//        when (command) {

//        is Command.IO.OpenSlave9600B8N1  -> openPort(command)
//        is Command.IO.OpenSlave19200B8N1 -> openPort(command)
//        is Command.IO.OpenSlave19200B9N1 -> openPort(command)
//        is Command.IO.CloseSlave         -> closePort(command)
//        is Command.IO.SendSlave          -> sendData(command)
//        is Command.IO.CirsaMode          -> masterMode(command)
    }

//    private suspend fun openPort(command: Command.IO){
//        serialManager.openPort(command)
//    }
//
//    private suspend fun closePort(command: Command.IO.CloseSlave){
//        serialManager.closePort(command)
//    }
//
//    private suspend fun sendData(command: Command.IO.SendSlave){
//        serialManager.sendData(command)
//    }
//
//    private fun masterMode(command: Command.IO){
//        serialManager.masterMode(command)
//    }

    override fun IdNack() {
        throw Exception("Placa no identificada")
    }

    override fun openLed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playVideo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateVideo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
