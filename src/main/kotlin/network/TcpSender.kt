package network

import Extensions.writeCommand
import kotlinx.coroutines.*
import kotlinx.coroutines.io.ByteWriteChannel
import org.koin.core.KoinComponent


class TcpSender(private val channel: IResponseChannel) : ITcpSender, KoinComponent {


    val scope = CoroutineScope(Dispatchers.IO)
    lateinit var output: ByteWriteChannel

    override fun output(input: ByteWriteChannel) {
        this.output = output
    }

    override fun run(): Job = scope.launch {
        while (isActive) {
            try {
                val byteArray = channel.receive()
                output.writeCommand(byteArray)
//                    byteArray.forEach { output.writeByte(it) }
            } catch (e: Throwable) {
                //LOGGING HERE
                println("No se puede enviar al servidor")
                e.printStackTrace()
            }
        }
    }


}