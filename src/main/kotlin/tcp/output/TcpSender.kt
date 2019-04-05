package tcp.output

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.io.ByteWriteChannel
import org.koin.core.KoinComponent


class TcpSender : ISender, KoinComponent {


    private lateinit var output: ByteWriteChannel
    override fun output(input: ByteWriteChannel) {
        this.output = output
    }

    private lateinit var channel: ReceiveChannel<ByteArray>
    override fun channel(channel: ReceiveChannel<ByteArray>) {
        this.channel = channel
    }

    override fun CoroutineScope.start() = launch {

        //SEND MAC ADDRESS/IDENTIFICATION (Micromaq message)
        while (isActive) {
            try {
                val byteArray = channel.receive()
//                tcp.output.writeCommand(byteArray)
//                    byteArray.forEach { tcp.output.writeByte(it) }
            } catch (e: Exception) {
                //LOGGING HERE
                println("No se puede enviar al servidor")
                e.printStackTrace()
            }
        }
    }
}