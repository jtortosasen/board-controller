package config

import java.io.File
import java.lang.Exception


interface IConfiguration{
    var serverIp: String
    var serverPort: Int
    var serialPort: String
    var pathMacAddress: String
    val macAddress: ByteArray
    val ftpUser: String
    val ftpPassword: String
    val ftpPort: Int
}

class Configuration : IConfiguration {
    override var serverIp: String = ""
    override var serverPort: Int = 0
    override var serialPort: String = ""
    override var pathMacAddress = ""
    override val macAddress: ByteArray
        get(){
            try{
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
            }catch (e: Exception){
                return byteArrayOf(0)
            }
        }
    override val ftpUser: String = "uXDo5ghxpQ8L"
    override val ftpPassword: String = "rWy9F1S4DSuj"
    override val ftpPort = 2121
}