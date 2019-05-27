package tcp.input

import command.Command
import command.extractCommand
import gpio.LedStrip
import gpio.LedManager
import kotlinx.coroutines.channels.SendChannel
import updater.Updater


interface IHandler {
    fun channel(channel: SendChannel<Command.IO>)
    suspend fun handle(data: ByteArray)
    suspend fun handleIO(command: Command.IO)
}

class NackException: Exception("Placa no identificada")
/**
 * Extrae el comando de los datos recibidos y enruta
 * Recibe [ledStrip] para controlar la tira de leds de premio
 * Recibe [handlerIOChannel] para enviar al componente IO todos los comandos que sean de tipo [Command.IO]
 */
class CommandHandler: IHandler {

    private val ledStrip = LedStrip()

    private lateinit var handlerIOChannel: SendChannel<Command.IO>
    override fun channel(channel: SendChannel<Command.IO>) {
        this.handlerIOChannel = channel
    }

    override suspend fun handle(data: ByteArray) {
        handleCommand(data.extractCommand())
    }

    private suspend fun handleCommand(command: Command) {
        when (command) {
            is Command.IO               -> handleIO(command)
            is Command.IdMacACK         -> {}
            is Command.IdMacNACK        -> idNack()
            is Command.OpenLedWhite     -> ledStrip.color = LedManager.LedColors.White
            is Command.OpenLedBlue      -> ledStrip.color = LedManager.LedColors.Blue
            is Command.OpenLedRed       -> ledStrip.color = LedManager.LedColors.Red
            is Command.OpenLedGreen     -> ledStrip.color = LedManager.LedColors.Green
            is Command.OpenLedLightBlue -> ledStrip.color = LedManager.LedColors.LightBlue
            is Command.OpenLedYellow    -> ledStrip.color = LedManager.LedColors.Yellow
            is Command.CloseLed         -> ledStrip.color = LedManager.LedColors.Off
            is Command.Restart          -> restart()
            is Command.Update           -> Updater().updateTesting()
            is Command.SwapProgram      -> swapProgram()
            is Command.PlayVideo        -> {}
            is Command.UpdateVideo      -> {}
            is Command.None             -> {}
        }
    }

    /**
     * Envia por un channel el comando extraído, al otro lado del channel está el receptor que se encargará del IO.
     * Este channel es no-bloqueante
     */
    override suspend fun handleIO(command: Command.IO) {
        handlerIOChannel.send(command)
    }

    /**
     * Si la placa no está identificada entonces lanzamos excepción para que el ciclo principal se interrumpa
     * y la placa vuelva a intentar conectarse.
     * @throws NackException
     */
    private fun idNack() {
        throw NackException()
    }

    private fun restart() {
        Runtime.getRuntime().exec("reboot")
        System.exit(0)
    }

    /**
     * Desactiva el servicio board.service y activa el servicio board-mono.service
     * Al reiniciar la placa ahora iniciará el servicio board-mono.service
     */
    private fun swapProgram(){
        Runtime.getRuntime().exec("systemctl disable board.service").waitFor()
        Runtime.getRuntime().exec("systemctl enable board-mono.service").waitFor()
    }
}