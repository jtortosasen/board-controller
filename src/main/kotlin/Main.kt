import com.beust.klaxon.Klaxon
import config.IConfiguration
import config.SettingsJson
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import java.io.File
import java.lang.Exception


class Application : KoinComponent {

    private val logger = KotlinLogging.logger {}

    suspend fun main() {
        try {
            val json = Klaxon().parse<SettingsJson>(File("settings.json"))

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
            networkManager.start()

        } catch (e: Exception) {
            logger.error { e }
        }
    }
}

suspend fun main() {
    Application().main()
}
