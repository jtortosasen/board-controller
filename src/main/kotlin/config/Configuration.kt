package config

class Configuration : IConfiguration {

    val serverIp: String = ""
    val serverPort: String = ""
    override var serialPort: String = ""
    val commandSize: String = ""
    val commandLengthAsBytes: Int = 4
    val commandPositionBytes: IntArray = intArrayOf(3, 5)
    val commandFixedLenghtBytes = 8

}