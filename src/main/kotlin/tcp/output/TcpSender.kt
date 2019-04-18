package tcp.output

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.io.ByteWriteChannel
import mu.KotlinLogging
import org.koin.core.KoinComponent
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

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

        identify(output)

        while (isActive) {
            try {
                val byteArray = channel.receive()
                logger.debug { "Getting byteArray" }
                byteArray.forEach { logger.debug { it.toUByte() } }
//                output.write(byteArray.size)
                val size = byteArray.size + 2
                val size1 = size and 0xff
                val size2 = size shr 8 and 0xff
                logger.debug { "Sending size as  $size2, $size1"}
                output.write(byteArrayOf(size2.toByte(), size1.toByte()))
                output.write(byteArray)
                delay(1000)
            } catch (e: Exception) {
                //LOGGING HERE
                println("No se puede enviar al servidor")
                e.printStackTrace()
                break
            }
        }
    }

    private fun identify(output: OutputStream){
        logger.debug { "Sending ID" }
        output.write(byteArrayOf(0x00, 0x0b))
        output.write(byteArrayOf(0x55, 0xFF.toByte(), 0x11, 0xFF.toByte(), 0x11, 0x03, 0x37, 0x73, 0x33))
    }
}