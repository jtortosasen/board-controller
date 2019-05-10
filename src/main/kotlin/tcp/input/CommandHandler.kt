package tcp.input

import command.Command
import command.extractCommand
import gpio.LedStrip
import gpio.LedManager
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging
import org.apache.commons.net.ftp.FTPClient
import updater.Updater
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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

    override suspend fun handleIO(command: Command.IO) {
        channel.send(command)
    }

    private fun idNack() {
        throw NackException()
    }

    private fun restart() {
        Runtime.getRuntime().exec("reboot")
        System.exit(0)
    }

    private fun swapProgram(){
        Runtime.getRuntime().exec("systemctl disable board.service").waitFor()
        Runtime.getRuntime().exec("systemctl enable board-mono.service").waitFor()
    }
}