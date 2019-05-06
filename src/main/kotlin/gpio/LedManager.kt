package gpio

import Gpio


class LedManager {

    enum class Led {
        Blue, Red, Green, LightBlue
    }
    private val ledStateBlue = Gpio(30)
    private val ledStateRed = Gpio(38)
    private val ledStateGreen = Gpio(40)

    /*
    * GPIO PIRULO
    */

//    private val ledStateBlue = Gpio(25)
//    private val ledStateRed = Gpio(26)
//    private val ledStateGreen = Gpio(27)

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
        Led.Blue -> ledStateBlue.switchWithOutputStream(0)
        Led.Red -> ledStateRed.switchWithOutputStream(0)
        Led.Green -> ledStateGreen.switchWithOutputStream(0)
        Led.LightBlue -> {
            ledStateBlue.switchWithOutputStream(0)
            ledStateGreen.switchWithOutputStream(0)
        }
    }

    private fun turnOnLed(led: Led) {
        when(led){
            Led.Blue -> ledStateBlue.switchWithOutputStream(1)
            Led.Red -> ledStateRed.switchWithOutputStream(1)
            Led.Green -> ledStateGreen.switchWithOutputStream(1)
            Led.LightBlue -> {
                ledStateBlue.switchWithOutputStream(1)
                ledStateGreen.switchWithOutputStream(1)
            }
        }
    }
}