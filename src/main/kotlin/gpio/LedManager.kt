package gpio

import Gpio

abstract class LedManager(val RGB:  Array<Gpio>){
    enum class LedColors {
        Blue, Red, Green, LightBlue, White, Off, Yellow
    }

    init{
        RGB.forEach {
            it.init("out")
            it.switch(1)
            it.switch(0)
        }
    }

    protected fun turnOn(led: LedColors){
        when (led) {
            LedColors.Red   -> RGB[0].switchWithOutputStream(1)
            LedColors.Green -> RGB[1].switchWithOutputStream(1)
            LedColors.Blue  -> RGB[2].switchWithOutputStream(1)
            LedColors.LightBlue -> {
                turnOn(LedColors.Blue)
                turnOn(LedColors.Green)
            }
            LedColors.White -> {
                turnOn(LedColors.Blue)
                turnOn(LedColors.Green)
                turnOn(LedColors.Red)
            }
            LedColors.Yellow -> {
                turnOn(LedColors.Green)
                turnOn(LedColors.Red)
            }
        }
    }
    protected fun turnOff(led: LedColors){
        when (led) {
            LedColors.Red   -> RGB[0].switchWithOutputStream(0)
            LedColors.Green -> RGB[1].switchWithOutputStream(0)
            LedColors.Blue  -> RGB[2].switchWithOutputStream(0)
            LedColors.LightBlue -> {
                turnOff(LedColors.Blue)
                turnOff(LedColors.Green)
            }
            LedColors.White -> {
                turnOff(LedColors.Blue)
                turnOff(LedColors.Green)
                turnOff(LedColors.Red)
            }
            LedColors.Yellow -> {
                turnOff(LedColors.Green)
                turnOff(LedColors.Red)
            }
        }
    }
}