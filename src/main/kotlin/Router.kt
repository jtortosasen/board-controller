import kotlinx.coroutines.channels.SendChannel


class Router {

    fun routeCommand(command: Command, sendChannel: SendChannel<ByteArray>): Unit =
        when (command){
            is Command.IO -> routeIO(command, sendChannel)
            is Command.OpenLed -> TODO()
            is Command.Restart -> TODO()
            is Command.Update -> TODO()
            is Command.PlayVideo -> TODO()
            is Command.UpdateVideo -> TODO()
        }

    private fun routeIO(ioCommand: Command.IO, sendChannel: SendChannel<ByteArray>): Unit =
            when(ioCommand){
                is Command.IO.OpenSlave9600B8N1 -> TODO()
                is Command.IO.OpenSlave19200B8N1 -> TODO()
                is Command.IO.OpenSlave19200B9N1 -> TODO()
                is Command.IO.CloseSlave -> TODO()
                is Command.IO.SendSlave -> TODO()
                is Command.IO.SerialState -> TODO()
                is Command.IO.DemoMode -> TODO()
                is Command.IO.CirsaMode -> TODO()
            }
}
