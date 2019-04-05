package tcp.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.io.ByteReadChannel

interface IListener {
    fun input(input: ByteReadChannel)
    fun CoroutineScope.start(): Job
}