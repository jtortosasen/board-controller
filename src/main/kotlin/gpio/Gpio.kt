package gpio

import java.io.FileOutputStream
import java.io.OutputStreamWriter


class GpioManager {

    enum class Led {
        Blue, Red, Green, LightBlue
    }
    private val ledBlue: OutputStreamWriter
    private val ledRed: OutputStreamWriter
    private val ledGreen:OutputStreamWriter
    var ledColor: Led = Led.Red
    set(value) {
        if(value != field){
            turnOnLed(led = value)
            field = value
        }
    }

    init{
        var gpio = OutputStreamWriter(FileOutputStream("/sys/class/gpio/export"))
        gpio.write(30)
        gpio.write(38)
        gpio.write(40)
        gpio.close()
        gpio = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio30/direction"))
        gpio.write("out")
        gpio.close()
        gpio = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio38/direction"))
        gpio.write("out")
        gpio.close()
        gpio = OutputStreamWriter(FileOutputStream("/sys/class/gpio/gpio40/direction"))
        gpio.write("out")
        gpio.close()

        ledRed = OutputStreamWriter(FileOutputStream("sys/class/gpio/gpio38/value"))
        ledBlue = OutputStreamWriter(FileOutputStream("sys/class/gpio/gpio30/value"))
        ledGreen = OutputStreamWriter(FileOutputStream("sys/class/gpio/gpio40/value"))
        ledColor = Led.Red
    }

    private fun turnOffLed(led: Led) = when(led){
        Led.Blue -> ledBlue on false
        Led.Red -> ledRed on false
        Led.Green -> ledGreen on false
        Led.LightBlue -> {
            ledBlue on false
            ledGreen on false
        }
    }

    private fun turnOnLed(led: Led) {
        turnOffLed(led)
        when(led){
            Led.Blue -> ledBlue on true
            Led.Red -> ledRed on true
            Led.Green -> ledGreen on true
            Led.LightBlue -> {
                ledBlue on true
                ledGreen on true
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