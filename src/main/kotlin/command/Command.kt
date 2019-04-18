package command

import com.fazecast.jSerialComm.SerialPort

sealed class Command {
    sealed class IO(val baudRate: Int = 0, val dataBits: Int = 0, val parity: Int = 0, val stopBits: Int = 0) : Command() {
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
            dataBits = 8,
            parity = SerialPort.NO_PARITY,
            stopBits = SerialPort.ONE_STOP_BIT
        )

        class CloseSlave : IO()
        class SendSlave(val content: ByteArray) : IO()

        sealed class MasterMode: IO(){
            class Cirsa : MasterMode()
            class Franco: MasterMode()
            class Sas: MasterMode()
        }
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
        fun get(command: Byte, content: ByteArray = byteArrayOf(0)): Command {
            return when (command) {
                0x22.toByte() -> IO.OpenSlave9600B8N1()
                0x23.toByte() -> IO.OpenSlave19200B8N1()
                0x24.toByte() -> IO.OpenSlave19200B9N1()
                0x25.toByte() -> IO.CloseSlave()
                0x45.toByte() -> IO.SendSlave(content)
                0x93.toByte() -> IO.MasterMode.Sas()
                0x30.toByte() -> OpenLed()
                0xF0.toByte() -> UpdateVideo()
                0xD0.toByte() -> PlayVideo()
                0xE0.toByte() -> Update()
                0x99.toByte() -> Restart()
                0x15.toByte() -> IdMacNACK()
                0x06.toByte() -> IdMacACK()
                else -> None()
            }
        }
    }
}