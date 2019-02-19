import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.writeAvailable
import kotlinx.coroutines.sync.Mutex
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicBoolean


class NetworkSocket(configuration: Configuration) {

    private val routerScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    private var isRunning = AtomicBoolean()
    var channelAvailable = AtomicBoolean()

    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort
    private val commandSize = configuration.commandSize.toInt()
    private val commandScheme = configuration.commandScheme

    private val router = Router()


    fun start() = GlobalScope.launch(Dispatchers.Main) {
        while (isRunning.get()) {
            try {
                val clientSocket = socketBuilder.connect(InetSocketAddress(serverIP, serverPort.toInt()))
                val channel = Channel<ByteArray>()
                val listenerJob = tcpListener(clientSocket.openReadChannel(), channel)
                val senderJob = tcpSender(clientSocket.openWriteChannel(), channel)
                listenerJob.join()
                listenerJob.cancel()
                senderJob.cancelAndJoin()
            } catch (e: Throwable) {
                println("No se puede conectar al servidor $e")
                delay(1000L)
            }
        }
    }

    private fun tcpSender(output: ByteWriteChannel, channel: Channel<ByteArray>) = CoroutineScope(Dispatchers.IO).launch{
        while(isActive){
            try{
                val byteArray = channel.receive()
                output.writeAvailable(byteArray)
            }catch (e: Throwable){
                println("No se puede enviar al servidor")
                e.printStackTrace()
            }
        }
    }

    private fun tcpListener(input: ByteReadChannel, channel: Channel<ByteArray>) = CoroutineScope(Dispatchers.IO).launch {
        channelAvailable.set(true)
        while (isActive) {
            try {
                val command = input.readCommand()
                if (command > 0)
                    routerScope.launch {
                        val c: Command? = Command.map[command.toInt()]
                        c?.let { router.routeCommand(it, channel) }
                    }
            } catch (e: Throwable) {
                channelAvailable.set(false)
                break
            }
        }
    }

    private suspend fun ByteReadChannel.readCommand(): Byte {
        val array = ByteArray(commandSize)
        try {
            while (true) {
                val firstByte = readByte()
                if (firstByte == 0x55.toByte()) {
                    array[0] = firstByte
                    for (i in 1..commandSize) {
                        array[i] = readByte()
                    }
                }
                return if (array[3] > 0 && (array[3] == array[5]))
                    array[3]
                else -1
            }
        } catch (e: Throwable) {
            throw Exception()
        }
    }
}