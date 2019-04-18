import config.IConfiguration
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import tcp.input.IListener
import tcp.output.ISender
import serial.ISerialManager
import java.net.Socket


class IOManager(configuration: IConfiguration) : KoinComponent {


    private val logger = KotlinLogging.logger {}

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort


    suspend fun start() {
        while (true) {
            try {
                logger.debug { "Connecting to TCP $serverIP: $serverPort" }

                val socket = Socket(serverIP, serverPort.toInt())
                socket.getOutputStream().write("Gestimaq\r\n".toByteArray(Charsets.US_ASCII) ,0, "Gestimaq\r\n".toByteArray(Charsets.US_ASCII).size)

                val serialManager: ISerialManager by inject()
                val listener: IListener by inject()
                val sender: ISender by inject()


                listener.input(socket.getInputStream())
                sender.output(socket.getOutputStream())

                val serialJob = serialManager.start()
                val listenerJob = listener.start()
                val senderJob = sender.start()

                logger.debug { "Running jobs"}
                senderJob.join()
                listenerJob.cancelAndJoin()
                logger.debug { "Canceling jobs"}
                serialJob.cancelAndJoin()
                logger.debug { "All jobs canceled"}
                delay(10000L)
            } catch (e: Exception) {
                //LOGGING HERE
                logger.error(e) {"$e"}
                delay(10000L)
            }
        }
    }
}