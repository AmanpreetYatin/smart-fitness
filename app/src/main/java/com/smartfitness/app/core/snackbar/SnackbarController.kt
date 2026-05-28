package com.smartfitness.app.core.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class SnackbarType { SUCCESS, ERROR, INFO }

data class SnackbarEvent(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    suspend fun showSuccess(message: String) {
        _events.send(SnackbarEvent(message, SnackbarType.SUCCESS))
    }

    suspend fun showError(message: String) {
        _events.send(SnackbarEvent(message, SnackbarType.ERROR))
    }

    suspend fun showInfo(message: String) {
        _events.send(SnackbarEvent(message, SnackbarType.INFO))
    }
}
