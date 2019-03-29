package network

import Extensions.readCommand
import kotlinx.coroutines.*
import kotlinx.coroutines.io.ByteReadChannel
import org.koin.core.KoinComponent


/**
 * Inicia el listener, gestiona el estado de conexión y abre y cierra el channel por la cual las corrutinas pueden enviar información
 * Recibe un comando, lo parsea y lo envía al enrutador para que controle qué ha de hacer
 */

class TcpListener(private val router: IRouter) : ITcpListener, KoinComponent {

    private lateinit var input: ByteReadChannel
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun input(input: ByteReadChannel) {
        this.input = input
    }

    override fun run(): Job = scope.launch {
        while (isActive) {
            try {
                val command = input.readCommand()
                command?.let {
                    router.routeCommand(it)
                }
            } catch (e: Exception) {
                break
            }
        }
    }


}