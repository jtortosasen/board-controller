package tcp.input

import command.Command
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging
import java.io.IOException


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
        is Command.IdMacNACK    -> IdNack()
        is Command.OpenLed      -> openLed()
        is Command.Restart      -> restart()
        is Command.Update       -> update()
        is Command.PlayVideo    -> playVideo()
        is Command.UpdateVideo  -> updateVideo()
        is Command.IdMacACK     -> {}
        is Command.IdMacNACK    -> throw Exception("No identified")
        is Command.None         -> {}
    }

    override suspend fun handleIO(command: Command.IO) {
        channel.send(command)
    }


    override fun IdNack() {
        throw Exception("Placa no identificada")
    }

    override fun openLed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restart() {
        shutdown()
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playVideo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateVideo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun ByteArray.extractCommand(): Command {
        try {
            val array = this
            if(array.size == 1){
                return Command.get(command = array[0])
            }
            if(array.size == 6){
                if(array.contains(0xFF.toByte())){
                    return Command.get(command = array[array.indexOf(0xFF.toByte()) + 1])
                }
            }else{
                if(array.contains(0xFF.toByte())){
                    return Command.get(
                        command = array[array.indexOf(0xFF.toByte()) + 1],
                        content = array.takeLast(array.size - array.indexOf(0x03)).reversed().toByteArray()
                    )
                }
            }
            return Command.get(0)
        }
        catch (e: Exception){
            throw e
        }
    }

    @Throws(RuntimeException::class, IOException::class)
    fun shutdown() {
        val shutdownCommand: String
        val operatingSystem = System.getProperty("os.name")

        if ("Linux" == operatingSystem || "Mac OS X" == operatingSystem) {
            shutdownCommand = "shutdown -h now"
        } else if ("Windows" == operatingSystem) {
            shutdownCommand = "shutdown.exe -s -t 0"
        } else {
            throw RuntimeException("Unsupported operating system.")
        }

        Runtime.getRuntime().exec(shutdownCommand)
        System.exit(0)
    }
}
