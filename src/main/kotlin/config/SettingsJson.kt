package config

import com.beust.klaxon.Json

data class SettingsJson(
    @Json(name = "server_address")
    val serverAddress: String,
    @Json(name = "server_port")
    val serverPort: Int,
    @Json(name = "serial_port")
    val serialPort: String,
    @Json(name = "mac_path")
    val macPath: String,
    @Json(name = "stable_channel")
    val stableChannel: String,
    @Json(name = "testing_channel")
    val testingChannel: String
)