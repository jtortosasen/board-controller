import config.IConfiguration
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject


class Application : KoinComponent {

    private val networkManager: IOManager by inject()
    private val configuration: IConfiguration by inject()
    fun start() = networkManager.start()

    fun portName(name: String){
        configuration.serialPort = name
    }
}

suspend fun main(args: Array<String>) {

    startKoin {
        printLogger()
        modules(koinModule)
    }

    if(args.isNotEmpty()){
        when {
            args[0] == "local" -> with(Application()){
                portName("/dev/ttyS0")
                start().join()
            }
            args[0] == "remote" -> with(Application()){
                portName("/dev/ttAMA4")
                start().join()
            }
            else -> print("args: [local|remote]")
        }
        return
    }

    print("args: [local|remote]")

}
