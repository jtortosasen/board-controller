package gpio

import Gpio
import mu.KotlinLogging

class LedStrip: LedManager(RGB = arrayOf(Gpio(26), Gpio(27), Gpio(25))) {

    @Volatile
    var color: LedColors = LedColors.Off
        set(value) {
            if (value != field) {
                if(value == LedColors.Off){
                    turnOff(led = field)
                }else{
                    turnOff(led = field)
                    turnOn(led = value)
                }
            }
            field = value
        }
}