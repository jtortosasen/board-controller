import kotlinx.coroutines.channels.SendChannel


class Router {

    val ioManager: IOManager = IOManager()

    fun routeCommand(command: Command, sendChannel: SendChannel<ByteArray>): Unit =
        when (command) {
            is Command.IO -> ioManager.routeIO(command, sendChannel)
            is Command.OpenLed -> TODO()
            is Command.Restart -> TODO()
            is Command.Update -> TODO()
            is Command.PlayVideo -> TODO()
            is Command.UpdateVideo -> TODO()
        }
}
