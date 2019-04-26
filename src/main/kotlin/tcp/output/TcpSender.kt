package tcp.output

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import mu.KotlinLogging
import org.koin.core.KoinComponent
import java.io.OutputStream


interface ISender {
    fun channel(channel: ReceiveChannel<ByteArray>)
    fun output(output: OutputStream)
    suspend fun start(): Job
}


class TcpSender : ISender, KoinComponent {

    private val logger = KotlinLogging.logger {  }

    private lateinit var output: OutputStream
    override fun output(output: OutputStream) {
        this.output = output
    }

    private lateinit var channel: ReceiveChannel<ByteArray>
    override fun channel(channel: ReceiveChannel<ByteArray>) {
        this.channel = channel
    }

    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        output.write(byteArrayOf(0x00, 0x0b))
        output.write(byteArrayOf(0x55, 0xFF.toByte(), 0x11, 0xFF.toByte(), 0x11, 0x03, 0x37, 0x73, 0x23))
        while (isActive) {
            try {
                val byteArray = channel.receive()

                val size = byteArray.size + 2
                val size1 = size and 0xff
                val size2 = size shr 8 and 0xff

                output.write(byteArrayOf(size2.toByte(), size1.toByte()))
                output.write(byteArray)
                delay(1000)
            } catch (e: Exception) {
                logger.debug {"Can't send to server"}
                e.printStackTrace()
                break
            }
        }
    }
}