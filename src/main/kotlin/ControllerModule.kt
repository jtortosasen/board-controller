import network.*
import org.koin.dsl.module

val controllerModule = module {


    single<INetworkConfiguration> { NetworkConfiguration() }
    single<IResponseChannel> { ResponseChannel() }
    single { Router() }

    factory<ITcpListener> { TcpListener(get()) }
    factory<ITcpSender> { TcpSender(get()) }

    single { NetworkManager(get(), get()) }
}