package network

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.InetSocketAddress

/**
 * network.NetworkManager se encarga de gestionar la comunicación vía TCP, provee un punto de entrada y otro de salida,
 * utiliza un channel para recibir de cualquier coroutina información que ha de enviarse al servidor  externo
 */

class NetworkManager(configuration: NetworkConfiguration, private val router: Router) : KoinComponent {

    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort

    /**
     * Se encarga de iniciar el listener y el sender, es el punto de entrada del programa.
     * Crea un socket de conexión al servidor, crea corrutinas de escucha y envío y gestiona la conexión,
     * cuando la pierde, fuerza otra vez a conectarse una y otra vez.
     */

    fun start() = GlobalScope.launch(Dispatchers.Main) {
        while (isActive) {
            try {
                val socket = socketBuilder.connect(InetSocketAddress(serverIP, serverPort.toInt()))

                val listener: ITcpListener by inject()
                val sender: ITcpSender by inject()

                listener.input(socket.openReadChannel())
                sender.output(socket.openWriteChannel())

                val listenerJob = listener.run()
                val senderJob = sender.run()

                // suspendemos la ejecución aquí a la espera de que ocurra cualquier evento o fallo que finalice la escucha,
                // cancelamos todas las corrutinas que cuelgan de la principal y volvemos a iniciar la conexión

                listenerJob.join()
                listenerJob.cancelChildren()
                senderJob.cancelAndJoin()
            } catch (e: Exception) {
                //LOGGING HERE
                println("No se puede conectar al servidor $e")
                delay(1000L)
            }
        }
    }

}