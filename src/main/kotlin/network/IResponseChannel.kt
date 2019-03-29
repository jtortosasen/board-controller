package network

interface IResponseChannel {
    fun receive(): ByteArray
}