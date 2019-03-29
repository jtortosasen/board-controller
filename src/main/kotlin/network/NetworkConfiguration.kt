package network

class NetworkConfiguration : INetworkConfiguration {
    val serverIp: String = ""
    val serverPort: String = ""
    val commandSize: String = ""
    val commandLengthAsBytes: Int = 4
    val commandPositionBytes: IntArray = intArrayOf(3, 5)
    val commandFixedLenghtBytes = 8
}