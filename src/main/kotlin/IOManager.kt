import config.Configuration
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import tcp.input.IListener
import tcp.output.ISender
import serial.ISerialManager
import java.net.InetSocketAddress


@KtorExperimentalAPI
class IOManager(configuration: Configuration) : KoinComponent {


    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort


    fun start() = CoroutineScope(Dispatchers.Main).launch {
        while (isActive) {
            try {
                val socket = socketBuilder.connect(InetSocketAddress(serverIP, serverPort.toInt()))

                val serialManager: ISerialManager by inject()
                val listener: IListener by inject()
                val sender: ISender by inject()

                listener.input(socket.openReadChannel())
                sender.output(socket.openWriteChannel())

                val serialJob = serialManager.run {
                    start()
                }
                val listenerJob = listener.run {
                    start()
                }
                val senderJob = sender.run {
                    start()
                }

                listenerJob.join()
                listenerJob.cancelChildren()
                serialJob.cancelAndJoin()
                senderJob.cancelAndJoin()
            } catch (e: Exception) {
                //LOGGING HERE
                println("No se puede conectar al servidor $e")
                delay(1000L)
            }
        }
    }
}