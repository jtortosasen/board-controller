package network

import Command

interface IRouter {
    fun routeCommand(command: Command)
}