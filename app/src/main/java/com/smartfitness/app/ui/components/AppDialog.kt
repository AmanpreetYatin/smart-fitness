package com.smartfitness.app.ui.components
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        },

        text = {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },

        confirmButton = {

            // ✅ Primary Button (Filled)
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive)
                        Color.Red
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmText, color = Color.White)
            }
        },

        dismissButton = {

            // ✅ Secondary Button (White / Transparent)
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    dismissText,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}