package gpio

import Gpio


class LedState: LedManager(red = Gpio(38), green = Gpio(40), blue = Gpio(30)) {

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
        rgb[0].switch(1)
    }

}