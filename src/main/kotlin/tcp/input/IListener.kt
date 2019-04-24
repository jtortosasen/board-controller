package tcp.input

import kotlinx.coroutines.Job
import kotlinx.coroutines.io.ByteReadChannel
import java.io.InputStream


interface IListener {
    fun input(input: InputStream)
    suspend fun start(): Job
}