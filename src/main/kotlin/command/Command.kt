package command

import com.fazecast.jSerialComm.SerialPort
import java.util.*

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
            dataBits = 9,
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

    class OpenLedWhite : Command()
    class OpenLedBlue : Command()
    class OpenLedRed : Command()
    class OpenLedGreen : Command()
    class OpenLedLightBlue : Command()
    class OpenLedYellow : Command()
    class CloseLed: Command()
    class UpdateVideo : Command()
    class PlayVideo : Command()
    class Update : Command()
    class Restart : Command()
    class IdMacACK : Command()
    class IdMacNACK : Command()
    class SwapProgram: Command()
    class None : Command()

    companion object {
        fun get(command: Byte, content: ByteArray = byteArrayOf(0)): Command {
            return when (command) {
                0x22.toByte() -> IO.OpenSlave9600B8N1()
                0x23.toByte() -> IO.OpenSlave19200B8N1()
                0x24.toByte() -> IO.OpenSlave19200B9N1()
                0x25.toByte() -> IO.CloseSlave()
                0x45.toByte() -> IO.SendSlave(content = content)
                0x93.toByte() -> IO.MasterMode.Sas()
                0x30.toByte() -> OpenLedWhite()
                0x31.toByte() -> OpenLedBlue()
                0x32.toByte() -> OpenLedRed()
                0x33.toByte() -> OpenLedGreen()
                0x34.toByte() -> OpenLedLightBlue()
                0x35.toByte() -> OpenLedYellow()
                0x39.toByte() -> CloseLed()
                0xf0.toByte() -> UpdateVideo()
                0xd0.toByte() -> PlayVideo()
                0xe0.toByte() -> Update()
                0x99.toByte() -> Restart()
                0x15.toByte() -> IdMacNACK()
                0x06.toByte() -> IdMacACK()
                0xaf.toByte() -> SwapProgram()
                else -> None()
            }
        }
    }
}


fun ByteArray.extractCommand(): Command {
    val array = this

    val command22 = byteArrayOf(0x55, 0xFF.toByte(), 0x22, 0xFF.toByte(), 0x22, 0x03)
    val command23 = byteArrayOf(0x55, 0xFF.toByte(), 0x23, 0xFF.toByte(), 0x23, 0x03)
    val command24 = byteArrayOf(0x55, 0xFF.toByte(), 0x24, 0xFF.toByte(), 0x24, 0x03)
    val command25 = byteArrayOf(0x55, 0xFF.toByte(), 0x25, 0xFF.toByte(), 0x25, 0x03)
    val command45 = byteArrayOf(0x55, 0xFF.toByte(), 0x45, 0xFF.toByte(), 0x45, 0x03)
    val command93 = byteArrayOf(0x55, 0xFF.toByte(), 0x93.toByte(), 0xFF.toByte(), 0x93.toByte(), 0x03)
    val command30 = byteArrayOf(0x55, 0xFF.toByte(), 0x30.toByte(), 0xFF.toByte(), 0x30.toByte(), 0x03)
    val command31 = byteArrayOf(0x55, 0xFF.toByte(), 0x31.toByte(), 0xFF.toByte(), 0x31.toByte(), 0x03)
    val command32 = byteArrayOf(0x55, 0xFF.toByte(), 0x32.toByte(), 0xFF.toByte(), 0x32.toByte(), 0x03)
    val command33 = byteArrayOf(0x55, 0xFF.toByte(), 0x33.toByte(), 0xFF.toByte(), 0x33.toByte(), 0x03)
    val command34 = byteArrayOf(0x55, 0xFF.toByte(), 0x34.toByte(), 0xFF.toByte(), 0x34.toByte(), 0x03)
    val command35 = byteArrayOf(0x55, 0xFF.toByte(), 0x35.toByte(), 0xFF.toByte(), 0x35.toByte(), 0x03)
    val command39 = byteArrayOf(0x55, 0xFF.toByte(), 0x39.toByte(), 0xFF.toByte(), 0x39.toByte(), 0x03)

    if (array.size == 1) {
        return Command.get(command = array[0])
    } else {
        when {
            array have command22 -> {
                return Command.get(command = 0x22)
            }
            array have command23 -> {
                return Command.get(command = 0x23)
            }
            array have command24 -> {
                return Command.get(command = 0x24)
            }
            array have command25 -> {
                return Command.get(command = 0x25)
            }
            array have command45 -> {
                return Command.get(command = 0x45, content = Arrays.copyOfRange(array, array.indexOf(command45[command45.size - 1]) + 1, array.size))
            }
            array have command93 -> {
                return Command.get(command = 0x93.toByte())
            }
            array have command30 -> {
                return Command.get(command = 0x30.toByte())
            }
            array have command31 -> {
                return Command.get(command = 0x31.toByte())
            }
            array have command32 -> {
                return Command.get(command = 0x32.toByte())
            }
            array have command33 -> {
                return Command.get(command = 0x33.toByte())
            }
            array have command34 -> {
                return Command.get(command = 0x34.toByte())
            }
            array have command35 -> {
                return Command.get(command = 0x35.toByte())
            }
            array have command39 -> {
                return Command.get(command = 0x39.toByte())
            }
        }
    }
    return Command.get(0)
}

infix fun ByteArray.have(inner: ByteArray): Boolean{
    val outer = this
    if(outer.contains(inner[0])){
        var index = outer.indexOf(inner[0])
        for (byte in inner){
            if(outer.size > index)
                if(outer[index] != byte)
                    return false
            index++
        }
        return true
    }
    return false
}