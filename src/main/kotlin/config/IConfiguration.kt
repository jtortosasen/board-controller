package config

interface IConfiguration{
    val serverIp: String
    val serverPort: String
    var develop: Boolean
    val serialPort: String
}