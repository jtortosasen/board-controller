package config

import java.io.File


interface IConfiguration{
    val serverIp: String
    val serverPort: String
    var develop: Boolean
    val serialPort: String
}


class Configuration : IConfiguration {

    override val serverIp: String = "192.168.1.22"
    override val serverPort: String = "9923"
    override val serialPort: String
        get() {
            return if (develop)
                "/dev/ttyS0"
            else
                "/dev/ttyAMA4"
        }
    private val pathMacAddres: String
        get() {
            return if (develop)
                "/sys/class/net/enp2s0/address"
            else
                "/sys/class/net/wlan0/address"
        }
    val macAddress: ByteArray
            get(){
                val macString: String = File(pathMacAddres)
                    .inputStream()
                    .bufferedReader()
                    .readLine()
                    .replace(":", "")

                val data = ByteArray(macString.length / 2)
                var i = 0
                while (i < macString.length) {
                    data[i / 2] = ((Character.digit(macString[i], 16) shl 4) + Character.digit(macString[i + 1], 16)).toByte()
                    i += 2
                }
                return data
            }
    override var develop = false
//    val commandSize: String = ""
//    val commandLengthAsBytes: Int = 4
//    val commandPositionBytes: IntArray = intArrayOf(3, 5)
//    val commandFixedLenghtBytes = 8

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

}