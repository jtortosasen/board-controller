package tcp.output

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.io.ByteWriteChannel

interface ISender {
    fun channel(channel: ReceiveChannel<ByteArray>)
    fun output(output: ByteWriteChannel)
    fun CoroutineScope.start(): Job
}