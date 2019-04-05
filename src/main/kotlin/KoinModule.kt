import config.IConfiguration
import config.Configuration
import tcp.input.CommandHandler
import tcp.input.IHandler
import io.ktor.util.KtorExperimentalAPI
import org.koin.dsl.module
import tcp.input.IListener
import tcp.input.TcpListener
import tcp.output.ISender
import tcp.output.TcpSender
import serial.ISerialIO
import serial.ISerialManager
import serial.SerialIO
import serial.SerialManager


@KtorExperimentalAPI
val koinModule = module {

    single <IConfiguration> {
        Configuration()
    }

    single <ISerialIO> {
        SerialIO()
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
            serialIO = get<ISerialIO>() as SerialIO,
            config = get<IConfiguration>() as Configuration
        )
    }

    factory <IListener> {
        TcpListener(handler = get<IHandler>() as CommandHandler)
    }

    factory <ISender> {
        TcpSender()
    }
}