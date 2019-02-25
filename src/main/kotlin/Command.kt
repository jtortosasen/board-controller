sealed class Command{
    sealed class IO : Command(){
        class OpenSlave9600B8N1(baudRate: Int = 9600, bits: Int = 8) : IO()
        class OpenSlave19200B8N1(baudRate: Int = 19200, bits: Int = 8) : IO()
        class OpenSlave19200B9N1(baudRate: Int = 19200, bits: Int = 9) : IO()
        class CloseSlave: IO()
        class SendSlave(content: ByteArray) : IO()
        class SerialState: IO()
        class DemoMode: IO()
        class CirsaMode: IO()
    }

    class OpenLed: Command()
    class UpdateVideo: Command()
    class PlayVideo: Command()
    class Update: Command()
    class Restart: Command()

    companion object {
        fun get(command: Int, content: ByteArray): Command?{
            return when(command){
                0x22 -> IO.OpenSlave9600B8N1()
                0x23 -> IO.OpenSlave19200B8N1()
                0x24 -> IO.OpenSlave19200B9N1()
                0x25 -> IO.CloseSlave()
                0x45 -> IO.SendSlave(content)
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