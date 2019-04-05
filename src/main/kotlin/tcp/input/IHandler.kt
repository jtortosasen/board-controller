package tcp.input

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel

interface IHandler {

    fun channel(channel: SendChannel<Command.IO>)
    suspend fun handle(data: ByteArray)
    suspend fun handleIO(command: Command.IO)
    fun IdNack()
    fun openLed()
    fun restart()
    fun update()
    fun playVideo()
    fun updateVideo()

}