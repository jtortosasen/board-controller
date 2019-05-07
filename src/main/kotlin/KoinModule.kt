import config.IConfiguration
import config.Configuration
import gpio.LedState
import tcp.input.CommandHandler
import tcp.input.IHandler
import org.koin.dsl.module
import tcp.input.IListener
import tcp.input.TcpListener
import tcp.output.ISender
import tcp.output.TcpSender
import serial.ISerialManager
import serial.SerialManager

@kotlin.ExperimentalUnsignedTypes
val koinModule = module {

    single { LedState() }

    single <IConfiguration> {
        Configuration()
    }

    single {
        IOManager(configuration = get<IConfiguration>() as Configuration)
    }

    single <IHandler> {
        CommandHandler()
    }

    factory <ISerialManager> {
        SerialManager(
            handle = get<IHandler>() as CommandHandler,
            sender = get<ISender>() as TcpSender,
            config = get<IConfiguration>() as Configuration
        )
    }

    single <IListener> {
        TcpListener(handler = get<IHandler>() as CommandHandler)
    }

    single <ISender> {
        TcpSender()
    }
}