import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.writeAvailable
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NetworkSocket se encarga de gestionar la comunicación vía TCP, provee un punto de entrada y otro de salida,
 * utiliza un channel para recibir de cualquier coroutina información que ha de enviarse al servidor  externo
 */

class NetworkSocket(configuration: Configuration) {

    private val routerScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private var isRunning = AtomicBoolean()
    var channelAvailable = AtomicBoolean()

    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()

    /**
     * Variables de configuración a servidor y tramas
     */

    private val serverIP = configuration.serverIp
    private val serverPort = configuration.serverPort
    private val commandLengthAsBytes = configuration.commandLengthAsBytes
    private val commandPositionBytes = configuration.commandPositionBytes
    private val commandFixedLenghtBytes = configuration.commandFixedLenghtBytes

    private val router = Router()

    /**
     * Se encarga de iniciar el listener y el sender, es el punto de entrada del programa.
     * Crea un socket de conexión al servidor, crea corrutinas de escucha y envío y gestiona la conexión,
     * cuando la pierde, fuerza otra vez a conectarse una y otra vez.
     */

    fun start() = GlobalScope.launch(Dispatchers.Main) {
        while (isRunning.get()) {
            try {
                val clientSocket = socketBuilder.connect(InetSocketAddress(serverIP, serverPort.toInt()))
                // channel por el cual las corrutinas se comunican con el sender y éste puede recibir lo que necesite
                val channel = Channel<ByteArray>()
                val listenerJob = tcpListener(clientSocket.openReadChannel(), channel)
                val senderJob = tcpSender(clientSocket.openWriteChannel(), channel)
                // suspendemos la ejecución aquí a la espera de que ocurra cualquier evento o fallo que finalice la escucha,
                // cancelamos todas las corrutinas que cuelgan de la principal y volvemos a iniciar la conexión
                listenerJob.join()
                listenerJob.cancelChildren()
                listenerJob.cancel()
                senderJob.cancelAndJoin()
            } catch (e: Throwable) {
                println("No se puede conectar al servidor $e")
                delay(1000L)
            }
        }
    }

    /**
     * Envía la información al servidor
     */

    private fun tcpSender(output: ByteWriteChannel, channel: Channel<ByteArray>) =
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val byteArray = channel.receive()
                    byteArray.forEach { output.writeByte(it) }
                } catch (e: Throwable) {
                    println("No se puede enviar al servidor")
                    e.printStackTrace()
                }
            }
        }

    /**
     * Inicia el listener, gestiona el estado de conexión y abre y cierra el channel por la cual las corrutinas pueden enviar información
     * Recibe un comando, lo parsea y lo envía al enrutador para que controle qué ha de hacer
     */

    private fun tcpListener(input: ByteReadChannel, channel: Channel<ByteArray>) =
        CoroutineScope(Dispatchers.IO).launch {
            channelAvailable.set(true)
            while (isActive) {
                try {
                    val command = input.readCommand()
                    command?.let {
                        routerScope.launch {
                            router.routeCommand(it, channel)
                        }
                    }
                } catch (e: Throwable) {
                    channelAvailable.set(false)
                    break
                }
            }
        }

    /**
     * Devuelve un array de 2 dimensiones con el comando y la trama  si la hay.
     * Primero recibe los dos primeros bytes, desplazamos el primer byte recibido 8 bits a la izquierda y le sumamos el segundo byte.
     */

    private suspend fun ByteReadChannel.readCommand(): Command? {
        try {
            val bufferLengthArray = ByteArray(commandLengthAsBytes) { readByte() }
            val remainingBufferLength =
                bufferLengthArray[0].toInt().shl(8) + bufferLengthArray[1].toInt()
            val bufferArray = ByteArray(remainingBufferLength) { readByte() }

            return if (bufferArray[commandPositionBytes[0]] == bufferArray[commandPositionBytes[1]])
                Command.get(bufferArray[3].toInt(),bufferArray.filterIndexed { index, _ -> index > 6 }.toByteArray())
            else null
        } catch (e: Throwable) {
            throw Exception()
        }
    }
}