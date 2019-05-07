package tcp.input

import command.Command
import command.extractCommand
import gpio.LedStrip
import gpio.LedManager
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging
import java.io.IOException


interface IHandler {
    fun channel(channel: SendChannel<Command.IO>)
    suspend fun handle(data: ByteArray)
    suspend fun handleIO(command: Command.IO)
}

class NackException: Exception("Placa no identificada")

class CommandHandler: IHandler {

    private val ledStrip = LedStrip()

    private lateinit var channel: SendChannel<Command.IO>
    override fun channel(channel: SendChannel<Command.IO>) {
        this.channel = channel
    }

    override suspend fun handle(data: ByteArray) {
        handleCommand(data.extractCommand())
    }

    private suspend fun handleCommand(command: Command) = when (command) {
        is Command.IO               -> handleIO(command)
        is Command.IdMacACK         -> {}
        is Command.IdMacNACK        -> IdNack()
        is Command.OpenLedWhite     -> ledStrip.color = LedManager.LedColors.White
        is Command.OpenLedBlue      -> ledStrip.color = LedManager.LedColors.Blue
        is Command.OpenLedRed       -> ledStrip.color = LedManager.LedColors.Red
        is Command.OpenLedGreen     -> ledStrip.color = LedManager.LedColors.Green
        is Command.OpenLedLightBlue -> ledStrip.color = LedManager.LedColors.LightBlue
        is Command.OpenLedYellow    -> ledStrip.color = LedManager.LedColors.Yellow
        is Command.CloseLed         -> ledStrip.color = LedManager.LedColors.Off
        is Command.Restart          -> restart()
        is Command.Update           -> {}
        is Command.SwapProgram      -> {}
        is Command.PlayVideo        -> {}
        is Command.UpdateVideo      -> {}
        is Command.None             -> {}
    }

    override suspend fun handleIO(command: Command.IO) {
        channel.send(command)
    }

    private fun IdNack() {
        throw NackException()
    }

    @Throws(RuntimeException::class, IOException::class)
    private fun restart() {
        val shutdownCommand: String
        val operatingSystem = System.getProperty("os.name")

        shutdownCommand = if ("Linux" == operatingSystem || "Mac OS X" == operatingSystem) {
            "reboot"
        } else if ("Windows" == operatingSystem) {
            "shutdown.exe -s -t 0"
        } else {
            throw RuntimeException("Unsupported operating system.")
        }
        Runtime.getRuntime().exec(shutdownCommand)
        System.exit(0)
    }
}