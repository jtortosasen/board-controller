package gpio

import Gpio


class LedState: LedManager(RGB = arrayOf(Gpio(38), Gpio(40), Gpio(30))) {

    @Volatile
    var color: LedColors = LedColors.Red
        set(value) {
            if (value != field) {
                turnOff(led = field)
                turnOn(led = value)
            }
            field = value
        }

    init{
        RGB[0].switch(1)
    }

}