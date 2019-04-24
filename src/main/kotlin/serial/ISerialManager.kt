package serial

import kotlinx.coroutines.Job


interface ISerialManager {
    suspend fun start(): Job
}