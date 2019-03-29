package network

import kotlinx.coroutines.Job
import kotlinx.coroutines.io.ByteReadChannel

interface ITcpListener {
    fun input(input: ByteReadChannel)
    fun run(): Job
}