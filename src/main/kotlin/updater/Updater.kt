package updater

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import config.IConfiguration
import mu.KotlinLogging
import org.apache.commons.net.ftp.FTPClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.io.File

/**
 *
 */
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


    /**
     * Actualiza el programa apuntando al directorio Testing de un servidor FTP alojado en el servidor
     * @return status
     */
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
            if (!client.listNames().contains(remoteFilename)){
                client.logout()
                return false
            }

            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            client.changeWorkingDirectory("testing")

            val fos = FileOutputStream("$homePath$filename")
            val status = client.retrieveFile(remoteFilename, fos)
            client.logout()
            return status

        } catch (e: Exception) {
            logger.error(e) { e }
            client.logout()
        }
        return false
    }


    /**
     * Actualiza el programa apuntando al directorio Stable de un servidor FTP alojado en el servidor
     * Recibe dos parametros que necesita para descartar descargar un ejecutable antiguo o entrar en un loop infinito
     * @param currentVersion versión actual del programa
     * @param currentJarName nombre del jar que está en ejecución
     * @return status
     */
    fun updateStable(currentVersion: Double, currentJarName: String): Boolean{
        val manifestName = "manifest.json"
        val filename = "board-controller"
        val homePath = "/home/artik/"
        val client = FTPClient()

        try {
            client.connect(ipAddress, port)

            if (!client.login(username, password)){
                logger.warn { "Can't loggin to FTP" }
                client.logout()
                return false
            }
            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            client.changeWorkingDirectory("stable")

            logger.debug { manifestName }
            client.listNames().forEach { print(it) }
            if (!client.listNames().contains(manifestName)){
                logger.warn { "$manifestName doesn't find" }
                client.logout()
                return false
            }

            val manifestOutputStream = FileOutputStream(File("manifest"))

            if(!client.retrieveFile(manifestName, manifestOutputStream)){
                logger.warn { "Can't download manifest" }
                client.logout()
                return false
            }
            logger.debug { "Manifest downloaded" }
            manifestOutputStream.close()

            val json = Klaxon().parse<ManifestJson>(File("manifest")) ?: return false

            logger.info { "Manifest parsed successfully" }

            if(currentVersion.toFloat() < json.currentVersion && client.listNames().contains(json.jarFile)) {
                logger.info { "Inferior version and contain jarfile" }
                if(currentJarName == "$filename${json.currentVersion}.jar"){
                    client.logout()
                    return false
                }

                val jarOutputStream = FileOutputStream("$homePath$filename${json.currentVersion}.jar")
                val status = client.retrieveFile(json.jarFile, jarOutputStream)

                client.logout()
                return status
            }
        } catch (e: Exception) {
            logger.error(e) {e}
            client.logout()
        }
        return false
    }
}