sealed class Command(val value: Int){
    sealed class IO(value: Int) : Command(value){
        class OpenSlave9600B8N1(value: Int = 0x22) : IO(value)
        class OpenSlave19200B8N1(value: Int = 0x23) : IO(value)
        class OpenSlave19200B9N1(value: Int = 0x24) : IO(value)
        class CloseSlave(value: Int = 0x25) : IO(value)
        class SendSlave(value: Int = 0x45, content: ByteArray) : IO(value)
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
        fun get(command: Int, content: ByteArray): Command?{
            return when(command){
                0x22 -> IO.OpenSlave9600B8N1()
                0x23 -> IO.OpenSlave19200B8N1()
                0x24 -> IO.OpenSlave19200B9N1()
                0x25 -> IO.CloseSlave()
                0x45 -> IO.SendSlave(content=content)
                0x40 -> IO.SerialState()
                0x85 -> IO.DemoMode()
                0x93 -> IO.CirsaMode()
                0x30 -> OpenLed()
                0xF0 -> UpdateVideo()
                0xD0 -> PlayVideo()
                0xE0 -> Update()
                0x99 -> Restart()
                else -> null
            }
        }
    }
}