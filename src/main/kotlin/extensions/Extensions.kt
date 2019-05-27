package extensions

/**
 * Elimina los ceros restantes\
 * @return array sin ceros
 */
fun IntArray.trim(): IntArray {
    var i: Int = size - 1
    while (i >= 0 && this[i] == 0) {
        --i
    }
    return this.copyOfRange(0, i + 1)
}

/**
 * Elimina los ceros restantes\
 * @return array sin ceros
 */
fun ByteArray.trim(): ByteArray {
    var i: Int = size - 1
    while (i >= 0 && this[i] == 0.toByte()) {
        --i
    }
    return this.copyOfRange(0, i + 1)
}