import config.IConfiguration
import gpio.LedManager
import gpio.LedState

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import tcp.input.IListener
import tcp.output.ISender
import serial.ISerialManager
import java.net.InetSocketAddress
import java.net.Socket

/**
 * IOManager se encarga de gestionar el ciclo de vida de los componentes TCP y serial del programa
 * @property configuration Configuración general inyectada por DI
 */
class IOManager(val configuration: IConfiguration) : KoinComponent {

    private val logger = KotlinLogging.logger {}

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort
    private val socketTimeout = 100_000

    /**
     * Loop principal
     * Conecta al server, instancia los componentes para recibir y enviar por TCP y el que se gestiona el puerto serie.
     * Al ser el punto de inicio, desde aquí se inyectan las dependencias para controlar el led y el stream del socket TCP
     * Inicia los componentes recibiendo un job y se engancha a ellos, si cualquiera de los 3 falla, terminan y el loop vuelve a empezar.
     */
    suspend fun start() {

        var connectionTries = 0

        while (true) {
            val ledState: LedState by inject()
            var socket: Socket? = null
            try {
                logger.debug { "Connecting to TCP $serverIP: $serverPort" }

                socket = Socket()
                socket.connect(InetSocketAddress(serverIP, serverPort), 10_000)
                logger.info { "Connected to server $serverIP:$serverPort" }
                // Enviamos Gestimaq para que el servidor detecte que no es una conexion "fantasma" o antigua
                socket.getOutputStream().write("Gestimaq\r\n".toByteArray(Charsets.US_ASCII) ,0, "Gestimaq\r\n".toByteArray(Charsets.US_ASCII).size)
                socket.soTimeout = socketTimeout
                connectionTries = 0

                val serialManager: ISerialManager by inject()
                val listener: IListener by inject()
                val sender: ISender by inject()

                ledState.color = LedManager.LedColors.LightBlue
                serialManager.led = ledState
                sender.led = ledState
                listener.led = ledState

                listener.input(socket.getInputStream())
                sender.output(socket.getOutputStream())
                logger.debug { configuration.macAddress.toList().toString() }
                sender.id(configuration.macAddress)

                val serialJob = serialManager.start()
                val listenerJob = listener.start()
                val senderJob = sender.start()

                logger.debug {"Running jobs"}

                listenerJob.join()

                ledState.color = LedManager.LedColors.Red

                logger.debug {"Canceling jobs"}

                serialManager.close()
                socket.close()
                senderJob.cancelAndJoin()
                serialJob.cancelAndJoin()

                logger.debug {"All jobs canceled"}
//                socket.close()

                //Tiempo espera entre conexiones, equivalente a Thread.sleep()
                delay(10000L)
            } catch (e: Exception) {
                ledState.color = LedManager.LedColors.Red
                logger.error(e) {e}
                if(connectionTries > 30){
                    logger.info { "Max connection attemps reached, rebooting" }
                    socket?.close()
                    Runtime.getRuntime().exec("reboot")
                    System.exit(0)
                }
                logger.info { "Connection attempts: $connectionTries" }
                connectionTries++
                //Tiempo espera entre conexiones, equivalente a Thread.sleep()
                delay(10000L)
            }
        }
    }
}