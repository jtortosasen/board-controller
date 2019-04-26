import config.IConfiguration
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject


class Application : KoinComponent {

    private val networkManager: IOManager by inject()
    private val configuration: IConfiguration by inject()
    suspend fun start() = networkManager.start()

    fun mode(development: Boolean){
        configuration.develop = development
    }
}

suspend fun main(args: Array<String>) {
    startKoin {
        printLogger()
        modules(koinModule)
    }

    if(args.isNotEmpty()){
        when {
            args[0] == "development" -> with(Application()){
                mode(development = true)
                start()
            }
            args[0] == "production" -> with(Application()){
                mode(development = false)
                start()
            }
            else -> print("args: [development|production]")
        }
    }
    print("args: [development|production]")
}
