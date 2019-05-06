import java.io.*

class Gpio(var pin: Int) {

    var port: String = "gpio$pin"

    private val direction: String
        get() {
            try {
                val p = Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "cat /sys/class/gpio/${this.port}/direction"))
                p.waitFor()
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                val text = StringBuilder()
                var line: String?

                while (true) {
                    line = reader.readLine()
                    if (line == null)
                        break
                    text.append(line)
                    text.append("\n")
                }
                return text.toString()
            } catch (e: IOException) {
                return ""
            }

        }
    private val state: Int
        get() {
            try {
                val p = Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "cat /sys/class/gpio/${this.port}/value"))
                p.waitFor()
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                val text = StringBuilder()
                var line: String?
                while (true) {
                    line = reader.readLine()
                    if (line == null)
                        break
                    text.append(line)
                    text.append("\n")
                }
                return try {
                    val retour = text.toString()
                    if (retour == "") {
                        -1
                    } else {
                        Integer.parseInt(retour.substring(0, 1))
                    }
                } catch (nfe: NumberFormatException) {
                    -1
                }
            } catch (e: IOException) {
                return -1
            }

        }

    fun switch(value: Int): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "echo $value > /sys/class/gpio/${this.port}/value")).waitFor()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun switchWithOutputStream(value: Int): Boolean {
        return try {
            val fos = FileOutputStream(File("/sys/class/gpio/${this.port}/value"))
            fos.write('0'.toInt() + value)
            fos.close()
            true
        } catch (e: IOException){
            false
        }
    }

    private fun direction(direction: String): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "echo $direction > /sys/class/gpio/${this.port}/direction"))
                .waitFor()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun activate(): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "echo ${this.pin} > /sys/class/gpio/export")).waitFor()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun desactivate(): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", "echo ${this.pin} > /sys/class/gpio/unexport")).waitFor()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun init(direction: String): Int {
        var retour: Int
        var ret: Boolean

        retour = state
        if (retour == -1) {
            ret = desactivate()
            if (!ret) {
                retour = -1
            }
            ret = activate()
            if (!ret) {
                retour = -2
            }
        }
        val ret2 = this.direction
        if (!ret2.contains(direction)) {
            // set the direction (in or out)
            ret = direction(direction)
            if (ret == false) {
                retour = -3
            }
        }
        return retour
    }
}