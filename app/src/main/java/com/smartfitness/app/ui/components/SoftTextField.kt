package com.smartfitness.app.ui.components
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SoftTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isFocusable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit =  {}   // when set → read-only tap-to-open field
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 12.dp,
        color = Color.White
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    1.dp,
                    if (isFocused) Color(0xFF113DFA).copy(alpha = 0.5f)
                    else Color.Transparent,
                    RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {

            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                readOnly = !isFocusable,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else keyboardType
                ),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Black
                ),
                visualTransformation =
                    if (isPassword && !passwordVisible)
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                modifier = Modifier
                    .focusable(isFocusable)
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
                    // Block pointer events on BasicTextField when read-only so
                    // taps fall through to the Box's clickable instead
                    .then(
                        if (!isFocusable) Modifier.pointerInput(Unit) {}
                        else Modifier
                    )
            )
        }
    }
}