import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel


/**
 * Los modos master son cancelables, se lanza corrutina
 * Los modos slave NO son cancelables, son un simple método suspendido
 * Los dos modos son excluyentes, master espera a que el comando slave termine y entra, puede ser cancelado si entra un comando que lo excluya
 * addCommand gestiona el estado y estas condiciones, puede cancelar las corrutinas (job)
 */


class IOManager{

    private interface Mode
    private class Master: Mode
    private enum class SlaveAction{
        OPEN, CLOSE, SEND
    }
    private data class Slave(val action: SlaveAction): Mode



    private data class SerialConfiguration(val bauds: Int, val dataBits: Int, val parityBit: Char, val stopBit: Int)
    private data class IOMode(val mode: Mode, val serialConfiguration: SerialConfiguration?)

    private lateinit var currentCommand: Command
    private var workingState: Boolean = false
    private lateinit var currentMode: Mode
    private lateinit var commandJob: Job

    private fun routeIO(ioCommand: Command.IO): IOMode = when (ioCommand) {
            is Command.IO.OpenSlave9600B8N1 -> IOMode(Slave(SlaveAction.OPEN), SerialConfiguration(9600, 8, 'N', 1))
            is Command.IO.OpenSlave19200B8N1 -> IOMode(Slave(SlaveAction.OPEN),SerialConfiguration(19200, 8, 'N', 1))
            is Command.IO.OpenSlave19200B9N1 -> IOMode(Slave(SlaveAction.OPEN),SerialConfiguration(19200, 9, 'N', 1))
            is Command.IO.CloseSlave -> IOMode(Slave(SlaveAction.CLOSE), null)
            is Command.IO.SendSlave -> IOMode(Slave(SlaveAction.SEND), null)
            is Command.IO.SerialState -> TODO()
            is Command.IO.DemoMode -> IOMode(Master(), null)
            is Command.IO.CirsaMode -> IOMode(Master(),null)
    }

    fun writeCommand(command: Command, channel: SendChannel<ByteArray>){

    }

    //Serial here


}