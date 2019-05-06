package tcp.input

import command.Command
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging
import java.io.IOException
import java.util.*


interface IHandler {
    fun channel(channel: SendChannel<Command.IO>)
    suspend fun handle(data: ByteArray)
    suspend fun handleIO(command: Command.IO)
}

class NackException: Exception("Placa no identificada")

class CommandHandler: IHandler {

    private val logger = KotlinLogging.logger {}

    private lateinit var channel: SendChannel<Command.IO>
    override fun channel(channel: SendChannel<Command.IO>) {
        this.channel = channel
    }

    override suspend fun handle(data: ByteArray) {
        handleCommand(data.extractCommand())
    }

    private suspend fun handleCommand(command: Command) = when (command) {
        is Command.IO           -> handleIO(command)
        is Command.IdMacACK     -> {}
        is Command.IdMacNACK    -> IdNack()
        is Command.OpenLed      -> openLed(rgb = command.color)
        is Command.CloseLed     -> closeLed()
        is Command.Restart      -> restart()
        is Command.Update       -> update()
        is Command.PlayVideo    -> playVideo()
        is Command.UpdateVideo  -> updateVideo()
        is Command.None         -> {}
    }

    override suspend fun handleIO(command: Command.IO) {
        channel.send(command)
    }

    private fun IdNack() {
        throw NackException()
    }

    private fun openLed(rgb: ByteArray) {
    }

    private fun closeLed(){
    }

    private fun update() {
    }

    private fun playVideo() {
    }

    private fun updateVideo() {
    }

    private fun ByteArray.extractCommand(): Command {

        val command22 = byteArrayOf(0x55, 0xFF.toByte(), 0x22, 0xFF.toByte(), 0x22, 0x03)
        val command23 = byteArrayOf(0x55, 0xFF.toByte(), 0x23, 0xFF.toByte(), 0x23, 0x03)
        val command24 = byteArrayOf(0x55, 0xFF.toByte(), 0x24, 0xFF.toByte(), 0x24, 0x03)
        val command25 = byteArrayOf(0x55, 0xFF.toByte(), 0x25, 0xFF.toByte(), 0x25, 0x03)
        val command45 = byteArrayOf(0x55, 0xFF.toByte(), 0x45, 0xFF.toByte(), 0x45, 0x03)
        val command93 = byteArrayOf(0x55, 0xFF.toByte(), 0x93.toByte(), 0xFF.toByte(), 0x93.toByte(), 0x03)
        val array = this

        if (array.size == 1) {
            return Command.get(command = array[0])
        } else {
            when {
                array have command22 -> {
                    return Command.get(command = 0x22)
                }
                array have command23 -> {
                    return Command.get(command = 0x23)
                }
                array have command24 -> {
                    return Command.get(command = 0x24)
                }
                array have command25 -> {
                    return Command.get(command = 0x25)
                }
                array have command45 -> {
                    return Command.get(command = 0x45, content = Arrays.copyOfRange(array, array.indexOf(command45[command45.size - 1]) + 1, array.size))
                }
                array have command93 -> {
                    return Command.get(command = 0x93.toByte())
                }
            }
        }
        return Command.get(0)
    }

    private infix fun ByteArray.have(inner: ByteArray): Boolean{
        val outer = this
        if(outer.contains(inner[0])){
            var index = outer.indexOf(inner[0])
            for (byte in inner){
                if(outer.size > index)
                    if(outer[index] != byte)
                        return false
                index++
            }
            return true
        }
        return false
    }

    @Throws(RuntimeException::class, IOException::class)
    private fun restart() {
        val shutdownCommand: String
        val operatingSystem = System.getProperty("os.name")

        shutdownCommand = if ("Linux" == operatingSystem || "Mac OS X" == operatingSystem) {
            "shutdown -h now"
        } else if ("Windows" == operatingSystem) {
            "shutdown.exe -s -t 0"
        } else {
            throw RuntimeException("Unsupported operating system.")
        }
        Runtime.getRuntime().exec(shutdownCommand)
        System.exit(0)
    }
}