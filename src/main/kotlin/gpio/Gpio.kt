package gpio

import kotlinx.coroutines.delay
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class GpioManager {

    enum class Led {
        Blue, Red, Green, LightBlue
    }
    private val ledStateBlue: OutputStreamWriter
    private val ledStateRed: OutputStreamWriter
    private val ledStateGreen:OutputStreamWriter
//    private val gpioAlert: OutputStreamWriter
    var ledColor: Led = Led.Red
    set(value) {
        if(value != field){
            turnOffLed(led = field)
            turnOnLed(led = value)
            field = value
        }
    }

    private fun bashCommand(command: String){
        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", "-c", command)
        try{
            val process = processBuilder.start()
            process.waitFor()
        }catch (e: Exception){ }
    }

    init{
        bashCommand(command = "echo 30 > /sys/class/gpio/export")
        bashCommand(command = "echo 38 > /sys/class/gpio/export")
        bashCommand(command = "echo 40 > /sys/class/gpio/export")
        bashCommand(command = "echo out > /sys/class/gpio/gpio30/direction")
        bashCommand(command = "echo out > /sys/class/gpio/gpio38/direction")
        bashCommand(command = "echo out  > /sys/class/gpio/gpio40/direction")

        ledStateRed = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio38/value"))
        ledStateBlue = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio30/value"))
        ledStateGreen = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio40/value"))
        ledColor = Led.Red
    }

    private fun turnOffLed(led: Led) = when(led){
        Led.Blue -> ledStateBlue on false
        Led.Red -> ledStateRed on false
        Led.Green -> ledStateGreen on false
        Led.LightBlue -> {
            ledStateBlue on false
            ledStateGreen on false
        }
    }

    private fun turnOnLed(led: Led) {
        when(led){
            Led.Blue -> ledStateBlue on true
            Led.Red -> ledStateRed on true
            Led.Green -> ledStateGreen on true
            Led.LightBlue -> {
                ledStateBlue on true
                ledStateGreen on true
            }
        }
    }

    private infix fun OutputStreamWriter.on(value: Boolean){
        if(value){
            write(1)
        }else{
            write(0)
        }
    }
}