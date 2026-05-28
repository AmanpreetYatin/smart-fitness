package com.smartfitness.app.core.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SuccessGreen = Color(0xFF2E7D32)
private val ErrorRed     = Color(0xFFC62828)
private val InfoBlue     = Color(0xFF1565C0)

@Composable
fun GlobalSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        SnackbarController.events.collect { event ->
            hostState.currentSnackbarData?.dismiss()
            hostState.showSnackbar(
                message = "${event.type.tag}${event.message}"
            )
        }
    }

    SnackbarHost(
        hostState = hostState,
        modifier  = modifier
    ) { data ->
        val type         = typeFromTag(data.visuals.message)
        val cleanMessage = data.visuals.message.removePrefix(type.tag)
        val bgColor      = when (type) {
            SnackbarType.SUCCESS -> SuccessGreen
            SnackbarType.ERROR   -> ErrorRed
            SnackbarType.INFO    -> InfoBlue
        }

        Snackbar(
            containerColor = bgColor,
            contentColor   = Color.White,
            shape          = RoundedCornerShape(12.dp),
            modifier       = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text       = cleanMessage,
                color      = Color.White,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private val SnackbarType.tag get() = when (this) {
    SnackbarType.SUCCESS -> "[SUCCESS]"
    SnackbarType.ERROR   -> "[ERROR]"
    SnackbarType.INFO    -> "[INFO]"
}

private fun typeFromTag(message: String) = when {
    message.startsWith("[SUCCESS]") -> SnackbarType.SUCCESS
    message.startsWith("[ERROR]")   -> SnackbarType.ERROR
    else                            -> SnackbarType.INFO
}
