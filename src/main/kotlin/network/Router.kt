package network

import Command
import IOManager


class Router : IRouter {


    val ioManager: IOManager = IOManager()

    override fun routeCommand(command: Command): Unit =
        when (command) {
            is Command.IO          -> ioManager.routeIO(command)
            is Command.OpenLed     -> TODO()
            is Command.Restart     -> TODO()
            is Command.Update      -> TODO()
            is Command.PlayVideo   -> TODO()
            is Command.UpdateVideo -> TODO()
        }
}
