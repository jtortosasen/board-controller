package tcp.input

import extensions.trim
import gpio.LedManager
import gpio.LedState
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream


interface IListener {
    fun input(input: InputStream)
    suspend fun start(): Job
    var led: LedState
}

/**
 * Se encarga de estar a la escucha al stream que recibe, detecta las conexiones cerradas por el servidor.
 * Recibe [handler] gestiona los bytes recibidos
 * Recibe [input]
 * Recibe [led] se encarga de marcar el estado usando el led de notificaciones de la placa
 */
class TcpListener(private val handler: IHandler) : IListener, KoinComponent {

    private val logger = KotlinLogging.logger {}
    private lateinit var input: InputStream
    override lateinit var led: LedState

    override fun input(input: InputStream) {
        this.input = input
    }

    /**
     * Lee mediante una función de extension [readCommand] y envía lo recibido al handler
     * Si recibe un [Throwable] finaliza el loop
     */
    @kotlin.ExperimentalUnsignedTypes
    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            try {
                val data = input.readCommand()
                if (data.isEmpty())
                    break
                led.color = LedManager.LedColors.LightBlue
                logger.debug { "Recieved data: " }
                logger.debug { data.map { it.toUByte().toString(16) } }
                // Mandamos los datos al handler/router
                handler.handle(data)
                delay(1000)
            }catch (e: NackException){
                logger.warn { "NackException, Board not identified" }
                Runtime.getRuntime().exec("reboot")
                System.exit(0)
            } catch (e: Exception) {
                led.color = LedManager.LedColors.Red
                logger.error(e) { e }
                break
            }
        }
    }

    /**
     * Recoge un máximo de 255 bytes
     * @return array usando [trim] para eliminar los 0 restantes
     */
    private fun InputStream.readCommand(): ByteArray {
        val array = ByteArray(255)
        var readed = 0
        try {
            readed = DataInputStream(this).read(array)
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) { }
        return array.trim(readed)
    }
}