import network.NetworkManager
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject


class Application : KoinComponent {

    private val networkManager: NetworkManager by inject()
    fun start() = networkManager.start()
}


fun main() {
    startKoin {
        printLogger()
        modules(controllerModule)
    }
    Application().start()
}