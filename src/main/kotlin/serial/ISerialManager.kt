package serial

import kotlinx.coroutines.Job
import tcp.input.Command

interface ISerialManager {

    fun start() : Job
}