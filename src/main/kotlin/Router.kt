import kotlinx.coroutines.channels.SendChannel


class Router {
    fun routeCommand(command: Command, sendChannel: SendChannel<ByteArray>): Unit =
        when (command){
            is Command.IO -> routeIO(command, sendChannel)
            is Command.LedOpen -> TODO()
            is Command.Restart -> TODO()
            is Command.Update -> TODO()
            is Command.VideoOpen -> TODO()
            is Command.VideoUpdate -> TODO()
        }

    private fun routeIO(ioCommand: Command.IO, sendChannel: SendChannel<ByteArray>): Unit =
            when(ioCommand){
                is Command.IO.SlaveOpen9600B8N1 -> TODO()
                is Command.IO.SlaveOpen19200B8N1 -> TODO()
                is Command.IO.SlaveOpen19200B9N1 -> TODO()
                is Command.IO.SlaveClose -> TODO()
                is Command.IO.SlaveSend -> TODO()
                is Command.IO.SerialState -> TODO()
                is Command.IO.MasterDemo -> TODO()
                is Command.IO.MasterCirsa -> TODO()
            }
}
