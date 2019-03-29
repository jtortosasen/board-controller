package network

import kotlinx.coroutines.Job
import kotlinx.coroutines.io.ByteWriteChannel

interface ITcpSender {
    fun output(output: ByteWriteChannel)

    fun run(): Job
}