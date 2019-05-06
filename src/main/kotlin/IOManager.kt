import config.IConfiguration
import gpio.LedManager
import gpio.LedManager.Led

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import tcp.input.IListener
import tcp.output.ISender
import serial.ISerialManager
import java.net.Socket


class IOManager(val configuration: IConfiguration) : KoinComponent {

    private val logger = KotlinLogging.logger {}

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort

    suspend fun start() {

        while (true) {
            val ledManager: LedManager by inject()

            try {
                logger.debug { "Connecting to TCP $serverIP: $serverPort" }

                val socket = Socket(serverIP, serverPort)
                socket.getOutputStream().write("Gestimaq\r\n".toByteArray(Charsets.US_ASCII) ,0, "Gestimaq\r\n".toByteArray(Charsets.US_ASCII).size)
                socket.soTimeout = 100000

                val serialManager: ISerialManager by inject()
                val listener: IListener by inject()
                val sender: ISender by inject()

                ledManager.ledColor = Led.LightBlue
                serialManager.led = ledManager
                sender.led = ledManager
                listener.led = ledManager

                listener.input(socket.getInputStream())
                sender.output(socket.getOutputStream())
                logger.debug { configuration.macAddress.toList().toString() }
                sender.id(configuration.macAddress)

                val serialJob = serialManager.start()
                val listenerJob = listener.start()
                val senderJob = sender.start()

                logger.debug { "Running jobs"}

                listenerJob.join()

                ledManager.ledColor = Led.Red

                logger.debug {"Canceling jobs"}

                serialManager.close()
                senderJob.cancelAndJoin()
                serialJob.cancelAndJoin()

                logger.debug {"All jobs canceled"}

                delay(10000L)
            } catch (e: Exception) {
                ledManager.ledColor = Led.Red
                logger.error {"$e"}
                delay(10000L)
            }
        }
    }
}