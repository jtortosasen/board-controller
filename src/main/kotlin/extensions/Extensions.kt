package extensions

import tcp.input.Command
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import java.io.InputStream

fun ByteArray.extractCommand(): Command {
    try {
        val byteArray = this.trim()

        // 55 FF CM FF CM 03
        for (i in 0 until byteArray.size) {
            if (byteArray[i] == 0x55.toByte() && byteArray[i + 1] == 0xFF.toByte() && byteArray[i + 5] == 0x03.toByte()) {
                return Command.get(
                    byteArray[i + 2].toInt(),
                    byteArray.filterIndexed { index, _ -> index > i + 5 }.toByteArray()
                )
            }
        }
        return Command.get(0)
    }
    catch (e: Exception){
        throw e
    }
}

suspend fun ByteReadChannel.readStream(): ByteArray {
    TODO()
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

fun InputStream.readAndPrintSerial(){
    try {
        var count = 0
        while(true) {
            Thread.sleep(10)
            val trunk = read()
            if(trunk == 1)
                count++
            if(count >= 10)
                break
            if(trunk != 1)
                count = 0
            print(trunk.toChar())
        }
        println()
    }catch (e: Throwable){
        println()
    }
}
