import config.IConfiguration
import config.Configuration
import gpio.GpioManager
import tcp.input.CommandHandler
import tcp.input.IHandler
import org.koin.dsl.module
import tcp.input.IListener
import tcp.input.TcpListener
import tcp.output.ISender
import tcp.output.TcpSender
import serial.ISerialIO
import serial.ISerialManager
import serial.SerialIO
import serial.SerialManager


val koinModule = module {

    single { GpioManager() }

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

    single <ISerialManager> {
        SerialManager(
            handle = get<IHandler>() as CommandHandler,
            sender = get<ISender>() as TcpSender,
            serialIO = get<ISerialIO>() as SerialIO,
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