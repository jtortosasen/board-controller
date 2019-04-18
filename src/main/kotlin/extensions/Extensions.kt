package extensions

import command.Command




fun IntArray.trim(): IntArray {
    var i: Int = size - 1
    while (i >= 0 && this[i] == 0) {
        --i
    }
    return this.copyOfRange(0, i + 1)
}

fun ByteArray.trim(): ByteArray {
    var i: Int = size - 1
    while (i >= 0 && this[i] == 0.toByte()) {
        --i
    }
    return this.copyOfRange(0, i + 1)
}