package Extensions

import Command
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.readAvailable

suspend fun ByteReadChannel.readCommand(): Command? {
    try {
        val tempArray = ByteArray(255)
        readAvailable(tempArray)
        val byteArray = tempArray.trim()

        // 55 FF CM FF CM 03
        for (i in 0 until byteArray.size) {
            if (byteArray[i] == 0x55.toByte() && byteArray[i + 1] == 0xFF.toByte() && byteArray[i + 5] == 0x03.toByte()) {
                return Command.get(
                    byteArray[i + 2].toInt(),
                    byteArray.filterIndexed { index, _ -> index > i + 5 }.toByteArray()
                )
            }
        }
    } catch (e: Throwable) {
        throw Exception()
    }
    return null
}

suspend fun ByteWriteChannel.writeCommand(byteArray: ByteArray) {
    try {
        val encapsulatedByteArray = ByteArray(byteArray.size + 2)
        TODO()

    } catch (e: Exception) {

    }
}

private fun ByteArray.trim(): ByteArray {
    var i: Int = size - 1
    while (i >= 0 && this[i].toInt() == 0) {
        --i
    }
    return this.copyOfRange(0, i + 1)
}
