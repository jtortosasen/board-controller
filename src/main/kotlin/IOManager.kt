import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.Job


/**
 * Los modos master son cancelables, se lanza corrutina
 * Los modos slave NO son cancelables, son un simple método suspendido
 * Los dos modos son excluyentes, master espera a que el comando slave termine y entra, puede ser cancelado si entra un comando que lo excluya
 * addCommand gestiona el estado y estas condiciones, puede cancelar las corrutinas (job)
 */


class IOManager {

    private lateinit var currentCommand: Command
    private var workingState: Boolean = false
    private lateinit var commandJob: Job

    private val serialPorts = SerialPort.getCommPorts()
    private lateinit var serialPort: SerialPort


    fun routeIO(command: Command.IO): Unit = when (command) {
        is Command.IO.OpenSlave9600B8N1  -> TODO()
        is Command.IO.OpenSlave19200B8N1 -> TODO()
        is Command.IO.OpenSlave19200B9N1 -> TODO()
        is Command.IO.CloseSlave         -> TODO()
        is Command.IO.SendSlave          -> TODO()
        is Command.IO.SerialState        -> serialState()
        is Command.IO.DemoMode           -> TODO()
        is Command.IO.CirsaMode          -> TODO()
    }

    private fun serialState() {
        if (!this::serialPort.isInitialized) {
            if (serialPorts.size > 0) {
                serialPort = serialPorts[0]
                //SEND ACK
                return
            } else {
                //SEND NACK
                return
            }
        }
        //SEND ACK
        return
    }

//    private fun openSlave9600B8N1


    private fun configureSerialConnection() {

    }


    //Serial here


}