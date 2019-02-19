sealed class Command(val value: Int){
    sealed class IO(value: Int) : Command(value){
        class SlaveOpen9600B8N1(value: Int = 0x22) : IO(value)
        class SlaveOpen19200B8N1(value: Int = 0x23) : IO(value)
        class SlaveOpen19200B9N1(value: Int = 0x24) : IO(value)
        class SlaveClose(value: Int = 0x25) : IO(value)
        class SlaveSend(value: Int = 0x45) : IO(value)
        class SerialState(value: Int = 0x40): IO(value)
        class MasterDemo(value: Int = 0x85): IO(value)
        class MasterCirsa(value: Int = 0x93): IO(value)
    }

    class LedOpen(value: Int = 0x30): Command(value)
    class VideoUpdate(value: Int = 0xF0): Command(value)
    class VideoOpen(value: Int = 0xD0): Command(value)
    class Update(value: Int = 0xE0): Command(value)
    class Restart(value: Int = 0x99): Command(value)

    companion object {
        val map = hashMapOf(
            0x22 to IO.SlaveOpen9600B8N1(),
            0x23 to IO.SlaveOpen19200B8N1(),
            0x24 to IO.SlaveOpen19200B9N1(),
            0x25 to IO.SlaveClose(),
            0x45 to IO.SlaveSend(),
            0x40 to IO.SerialState(),
            0x85 to IO.MasterDemo(),
            0x93 to IO.MasterCirsa(),
            0x30 to LedOpen(),
            0xF0 to VideoUpdate(),
            0xD0 to VideoOpen(),
            0xE0 to Update(),
            0x99 to Restart()
        )
    }
}