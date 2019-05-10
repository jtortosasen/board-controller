package updater

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import config.IConfiguration
import mu.KotlinLogging
import org.apache.commons.net.ftp.FTPClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.FileOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class ManifestJson(
    @Json(name = "current_version")
    val currentVersion: Double,
    @Json(name = "jar_file")
    val jarFile: String
)

class Updater: KoinComponent{

    private val config: IConfiguration by inject()

    private val username = config.ftpUser
    private val password = config.ftpPassword
    private val port = config.ftpPort
    private val ipAddress = config.serverIp

    private val logger = KotlinLogging.logger {}


    fun updateTesting(): Boolean{
        val datetime = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val remoteFilename = "board-controller.jar"
        val filename = "board-controller-TESTING${datetime}.jar"
        val homePath = "/home/artik/"
        val client = FTPClient()

        try {
            client.connect(ipAddress, port)
            if (!client.login(username, password) )
                return false
            if (!client.listNames().contains(remoteFilename))
                return false

            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            client.changeWorkingDirectory("testing")

            val fos = FileOutputStream(homePath + filename)
            return client.retrieveFile(remoteFilename, fos)

        } catch (e: Exception) {
            logger.error(e) { e }
        }
        return false
    }

    fun updateStable(currentVersion: Double): Boolean{
        val manifestName = "manifest.json"
        val filename = "board-controller"
        val homePath = "/home/artik/"
        val client = FTPClient()

        try {
            client.connect(ipAddress, port)

            if (!client.login(username, password))
                return false
            if (!client.listNames().contains(manifestName))
                return false

            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            client.changeWorkingDirectory("stable")

            val pipedOutputStream = PipedOutputStream()
            val pipedInputStream = PipedInputStream()

            pipedInputStream.connect(pipedOutputStream)

            if(!client.retrieveFile(manifestName, pipedOutputStream))
                return false

            val json = Klaxon().parse<ManifestJson>(pipedInputStream) ?: return false

            if(currentVersion.toFloat() <= json.currentVersion && client.listNames().contains(json.jarFile)) {
                val fos = FileOutputStream("$homePath$filename${json.currentVersion}.jar")

                return client.retrieveFile(json.jarFile, fos)
            }
        } catch (e: Exception) {
            logger.error(e) {e}
        }
        return false
    }
}