package tcp.input

import extensions.readStream
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent


class TcpListener(private val handler: IHandler) : IListener, KoinComponent {

    private lateinit var input: ByteReadChannel

    override fun input(input: ByteReadChannel) {
        this.input = input
    }

    override fun CoroutineScope.start() = launch {
        while (isActive) {
            try {
                val data = input.readStream()
                handler.handle(data)
            } catch (e: Exception) {
                break
            }
        }
    }
}