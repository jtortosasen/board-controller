package gpio

import Gpio


class LedManager {

    enum class Led {
        Blue, Red, Green, LightBlue
    }
    private val ledStateBlue = Gpio(30)
    private val ledStateRed = Gpio(38)
    private val ledStateGreen = Gpio(40)
//    private val gpioAlert: OutputStreamWriter
    @Volatile
    var ledColor: Led = Led.Red
    set(value) {
        if(value != field){
            turnOffLed(led = field)
            turnOnLed(led = value)
        }
        field = value
    }

    init {
        ledStateRed.init("out")
        ledStateGreen.init("out")
        ledStateBlue.init("out")

        ledStateBlue.switch(0)
        ledStateGreen.switch(0)
        ledStateRed.switch(0)

        ledStateRed.switch(1)
    }

    private fun turnOffLed(led: Led) = when(led){
        Led.Blue -> ledStateBlue.switch(0)
        Led.Red -> ledStateRed.switch(0)
        Led.Green -> ledStateGreen.switch(0)
        Led.LightBlue -> {
            ledStateBlue.switch(0)
            ledStateGreen.switch(0)
        }
    }

    private fun turnOnLed(led: Led) {
        when(led){
            Led.Blue -> ledStateBlue.switch(1)
            Led.Red -> ledStateRed.switch(1)
            Led.Green -> ledStateGreen.switch(1)
            Led.LightBlue -> {
                ledStateBlue.switch(1)
                ledStateGreen.switch(1)
            }
        }
    }
}