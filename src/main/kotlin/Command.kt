sealed class Command(val value: Int){
    sealed class IO(value: Int) : Command(value){
        class OpenSlave9600B8N1(value: Int = 0x22) : IO(value)
        class OpenSlave19200B8N1(value: Int = 0x23) : IO(value)
        class OpenSlave19200B9N1(value: Int = 0x24) : IO(value)
        class CloseSlave(value: Int = 0x25) : IO(value)
        class SendSlave(value: Int = 0x45) : IO(value)
        class SerialState(value: Int = 0x40): IO(value)
        class DemoMode(value: Int = 0x85): IO(value)
        class CirsaMode(value: Int = 0x93): IO(value)
    }

    class OpenLed(value: Int = 0x30): Command(value)
    class UpdateVideo(value: Int = 0xF0): Command(value)
    class PlayVideo(value: Int = 0xD0): Command(value)
    class Update(value: Int = 0xE0): Command(value)
    class Restart(value: Int = 0x99): Command(value)

    companion object {
        val map = hashMapOf(
            0x22 to IO.OpenSlave9600B8N1(),
            0x23 to IO.OpenSlave19200B8N1(),
            0x24 to IO.OpenSlave19200B9N1(),
            0x25 to IO.CloseSlave(),
            0x45 to IO.SendSlave(),
            0x40 to IO.SerialState(),
            0x85 to IO.DemoMode(),
            0x93 to IO.CirsaMode(),
            0x30 to OpenLed(),
            0xF0 to UpdateVideo(),
            0xD0 to PlayVideo(),
            0xE0 to Update(),
            0x99 to Restart()
        )
    }
}