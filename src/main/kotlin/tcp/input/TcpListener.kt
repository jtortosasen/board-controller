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

class TcpListener(private val handler: IHandler) : IListener, KoinComponent {

    private val logger = KotlinLogging.logger {}
    private lateinit var input: InputStream
    override lateinit var led: LedState

    override fun input(input: InputStream) {
        this.input = input
    }

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
                handler.handle(data)
                delay(1000)
            } catch (e: Exception) {
                led.color = LedManager.LedColors.Red
                logger.error(e) { e }
                break
            }
        }
    }

    private fun InputStream.readCommand(): ByteArray {
        val array = ByteArray(255)
        try {
            DataInputStream(this).read(array)
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) { }
        return array.trim()
    }
}