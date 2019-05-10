package tcp.output

import gpio.LedManager
import gpio.LedState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import mu.KotlinLogging
import org.koin.core.KoinComponent
import java.io.OutputStream


interface ISender {
    fun channel(channel: ReceiveChannel<ByteArray>)
    fun output(output: OutputStream)
    suspend fun start(): Job
    fun id(id: ByteArray)
    var led: LedState
}


class TcpSender : ISender, KoinComponent {

    private val logger = KotlinLogging.logger {  }
    private lateinit var mac: ByteArray

    private lateinit var output: OutputStream
    override fun output(output: OutputStream) {
        this.output = output
    }

    override fun id(id: ByteArray){
        mac = id.copyOfRange(Math.max(id.size - 3, 0), id.size)
    }

    private lateinit var channel: ReceiveChannel<ByteArray>
    override fun channel(channel: ReceiveChannel<ByteArray>) {
        this.channel = channel
    }

    override lateinit var led: LedState

    private fun applyHeader(first: ByteArray, second: ByteArray): ByteArray {
        val arrayWithHeader = ByteArray(first.size + second.size)
        for ((index, byte) in first.withIndex()) {
            arrayWithHeader[index] = byte
        }
        for ((index, byte) in second.withIndex()) {
            arrayWithHeader[index + first.size] = byte
        }
        return arrayWithHeader
    }

    override suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        identifyMacAddress()
        while (isActive) {
            try {
                val byteArray = channel.receive()
                led.color = LedManager.LedColors.LightBlue

                val size = byteArray.size + 2
                logger.debug { "size: $size" }
                val size1 = size and 0xff
                val size2 = size shr 8 and 0xff

                val array = applyHeader(byteArrayOf(size2.toByte(), size1.toByte()), byteArray)

                logger.debug { "Sending:" }
                array.forEach { print(it.toString(16)) }
                println()
                output.write(array)
                delay(1000)
            } catch (e: Exception) {
                logger.error(e) { e }
                break
            }
        }
    }

    private fun identifyMacAddress(){
        output.write(byteArrayOf(0x00, 0x0b))
        output.write(applyHeader(byteArrayOf(0x55, 0xff.toByte(), 0x11, 0xff.toByte(), 0x11, 0x03), mac))
    }
}