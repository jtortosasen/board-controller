import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel


/**
 * Los modos master son cancelables, se lanza corrutina
 * Los modos slave NO son cancelables, son un simple método suspendido
 * Los dos modos son excluyentes, master espera a que el comando slave termine y entra, puede ser cancelado si entra un comando que lo excluya
 * addCommand gestiona el estado y estas condiciones, puede cancelar las corrutinas (job)
 */


class IOManager{

    private lateinit var currentCommand: Command
    private var workingState: Boolean = false
    private lateinit var commandJob: Job


    fun routeIO(command: Command.IO, channel: SendChannel<ByteArray>): Unit = when (command) {
        is Command.IO.OpenSlave9600B8N1 -> TODO()
        is Command.IO.OpenSlave19200B8N1 -> TODO()
        is Command.IO.OpenSlave19200B9N1 -> TODO()
        is Command.IO.CloseSlave -> TODO()
        is Command.IO.SendSlave -> TODO()
        is Command.IO.SerialState -> TODO()
        is Command.IO.DemoMode -> TODO()
        is Command.IO.CirsaMode -> TODO()
    }

    private fun configureSerialConnection(){

    }


    //Serial here


}