package tcp.input

import extensions.trim
import gpio.GpioManager
import gpio.GpioManager.Led
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.InputStream


interface IListener {
    fun input(input: InputStream)
    suspend fun start(): Job
}


class TcpListener(private val handler: IHandler) : IListener, KoinComponent {

    private val logger = KotlinLogging.logger {}
    private lateinit var input: InputStream
    override fun input(input: InputStream) {
        this.input = input
    }

    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            try {
                val data = input.readCommand()
                if (data.isEmpty())
                    break
                logger.debug { "Recieved data: " }
                data.forEach {
                    val a = it
                    print(a.toUByte().toString(16))
                }
                println()
                handler.handle(data)
                delay(1000)
            } catch (e: Exception) {
                logger.error { e }
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
        } catch (e: EOFException) {
        }
        return array.trim()
    }
}