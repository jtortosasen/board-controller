import com.beust.klaxon.Klaxon
import config.IConfiguration
import config.SettingsJson
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import updater.Updater
import java.io.File
import java.lang.Exception

/**
 * Inicia el módulo de DI
 * Carga las opciones del fichero [settingsPath] y da valores al componente de configuración para ser leído por el resto del programa
 * Comprueba si hay actualizaciones y reinicia en caso de descargarse un nuevo jar
 * Inicia [IOManager] y se engancha a él
 */
class Application : KoinComponent {

    private val VERSION = 1.0

    private val logger = KotlinLogging.logger {}
                private val settingsPath = "/home/artik/settings.json"
                lateinit var jarName: String

                @ExperimentalUnsignedTypes
                suspend fun main() {
                    try {
                        val json = Klaxon().parse<SettingsJson>(File(settingsPath))

                        startKoin {
                            printLogger()
                            modules(koinModule)
                        }

                        val networkManager: IOManager by inject()
                        val config: IConfiguration by inject()

                        json?.let {
                config.pathMacAddress = it.macPath
                config.serialPort = it.serialPort
                config.serverIp = it.serverAddress
                config.serverPort = it.serverPort
            }
            var updateSuccess = false
            try{
                updateSuccess = Updater().updateStable(currentVersion = VERSION, currentJarName = jarName)
            }catch (e: Exception){
                logger.error(e) { e }
            }
            if(!updateSuccess){
                logger.info { "Starting application, no updates available" }
                networkManager.start()
            }
            else
                restart()

        } catch (e: Exception) {
            logger.error(e) { e }
        }
    }

    private fun restart() {
        Runtime.getRuntime().exec("reboot")
        System.exit(0)
    }
}

/**
 * Punto de entrada del programa
 * @param args max len 1, recibe el nombre del .jar
 */
@ExperimentalUnsignedTypes
suspend fun main(args : Array<String>) {
    if(args.size == 1){
        val app = Application()
        app.jarName = args[0]
        app.main()
    }else
        println("PASS NAME OF JAR AS AN ARGUMENT")
    println("BYE :)")
}
