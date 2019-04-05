package tcp.input

import com.fazecast.jSerialComm.SerialPort

sealed class Command {
    sealed class IO(val baudRate: Int, val dataBits: Int, val parity: Int, val stopBits: Int) : Command() {
        class OpenSlave9600B8N1 : IO(
            baudRate = 9600,
            dataBits = 8,
            parity = SerialPort.NO_PARITY,
            stopBits = SerialPort.ONE_STOP_BIT
        )

        class OpenSlave19200B8N1 : IO(
            baudRate = 19200,
            dataBits = 8,
            parity = SerialPort.NO_PARITY,
            stopBits = SerialPort.ONE_STOP_BIT
        )

        class OpenSlave19200B9N1 : IO(
            baudRate = 19200,
            dataBits = 9,
            parity = SerialPort.NO_PARITY,
            stopBits = SerialPort.ONE_STOP_BIT
        )

        class CloseSlave : IO(0,0,0,0)
        class SendSlave(val content: ByteArray) : IO(0,0,0,0)
        class CirsaMode : IO(
            baudRate = 19200,
            dataBits = 9,
            parity = SerialPort.NO_PARITY,
            stopBits = SerialPort.ONE_STOP_BIT
        )
    }

    class OpenLed : Command()
    class UpdateVideo : Command()
    class PlayVideo : Command()
    class Update : Command()
    class Restart : Command()
    class IdMacACK : Command()
    class IdMacNACK : Command()
    class None : Command()

    companion object {
        fun get(command: Int, content: ByteArray = byteArrayOf(0)): Command {
            return when (command) {
                0x22 -> IO.OpenSlave9600B8N1()
                0x23 -> IO.OpenSlave19200B8N1()
                0x24 -> IO.OpenSlave19200B9N1()
                0x25 -> IO.CloseSlave()
                0x45 -> IO.SendSlave(content)
                0x93 -> IO.CirsaMode()
                0x30 -> OpenLed()
                0xF0 -> UpdateVideo()
                0xD0 -> PlayVideo()
                0xE0 -> Update()
                0x99 -> Restart()
                0x15 -> IdMacNACK()
                0x06 -> IdMacACK()
                else -> None()
            }
        }
    }
}