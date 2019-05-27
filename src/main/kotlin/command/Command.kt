package command

import com.fazecast.jSerialComm.SerialPort
import java.util.*

/**
 * Cada comando contiene información sobre qué acción debe hacer el programa
 * [IO] se relaciona con el I/O del puerto serie, apertura de serie con sus parámetros envio de datos y modo master o propio
 */
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

    /**
     * @param command el byte del comando
     * @param content el contenido si lo hay
     * @return el comando
     */
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

/**
 * @return [Command]
 */
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
    val commande0 = byteArrayOf(0x55, 0xFF.toByte(), 0xe0.toByte(), 0xFF.toByte(), 0xe0.toByte(), 0x03)
    val command99 = byteArrayOf(0x55, 0xFF.toByte(), 0x99.toByte(), 0xFF.toByte(), 0x99.toByte(), 0x03)
    val commandaf = byteArrayOf(0x55, 0xFF.toByte(), 0xaf.toByte(), 0xFF.toByte(), 0xaf.toByte(), 0x03)

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
            array have commande0 -> {
                return Command.get(command = 0xe0.toByte())
            }
            array have command99 -> {
                return Command.get(command = 0x99.toByte())
            }
            array have commandaf -> {
                return Command.get(command = 0xaf.toByte())
            }
        }
    }
    return Command.get(0)
}

/**
 * Extrae el comando del array
 * @param inner ByteArray que se necesita para saber si [this] contiene [inner]
 * @return true si [inner] está en [this]
 */
private infix fun ByteArray.have(inner: ByteArray): Boolean{
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