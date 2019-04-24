package tcp.output

import io.ktor.network.sockets.DatagramWriteChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.io.ByteWriteChannel
import java.io.OutputStream

interface ISender {
    fun channel(channel: ReceiveChannel<ByteArray>)
    fun output(output: OutputStream)
    suspend fun start(): Job
}