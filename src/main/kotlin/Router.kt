import kotlinx.coroutines.channels.SendChannel

class Router(val networkSocket: NetworkSocket) {

    fun routeCommand(command: Byte, sendChannel: SendChannel<ByteArray>): Unit = when (command.toInt()){
        //modo tunel
        0x22 -> TODO()
        0x23 -> TODO()
        0x24 -> TODO()
        0x25 -> TODO()
        0x45 -> TODO()

        //estado
        0x40 -> TODO()

        // propio
        0x85 -> TODO()
        0x93 -> TODO()

        //encendido led
        0x30 -> TODO()

        //actualizacion de software
        0xE0 -> TODO()

        //actualizacion de video
        0xF0 -> TODO()

        //ejecucion de video
        0xD0 -> TODO()


        //reiniciar
        0x99 -> TODO()
        else -> TODO()
    }
}