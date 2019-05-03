package config

import java.io.File


interface IConfiguration{
    var serverIp: String
    var serverPort: Int
    var serialPort: String
    var pathMacAddress: String
    var macAddress: ByteArray
}

class Configuration : IConfiguration {
    override var serverIp: String = ""
    override var serverPort: Int = 0
    override var serialPort: String = ""
    override var pathMacAddress = ""
    override var macAddress: ByteArray = byteArrayOf(0)
        get(){
            val macString: String = File(pathMacAddress)
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
}