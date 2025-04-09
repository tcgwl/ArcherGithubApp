package com.archer.github.app.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.archer.github.app.R

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    titleText: String = stringResource(R.string.dialog_title),
    confirmText: String = stringResource(R.string.dialog_button_confirm),
    cancelText: String = stringResource(R.string.dialog_button_cancel),
    showCancel: Boolean = true,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        onDismissRequest = { onDismiss() },
        title = { Text(text = titleText) },
        text = content,
        confirmButton = {
            Text(
                text = confirmText,
                color = Color.Red,
                modifier = Modifier.clickable {
                    onDismiss()
                    onConfirm()
                }
            )
        },
        dismissButton = {
            if (showCancel) {
                Text(text = cancelText, modifier = Modifier.clickable {
                    onDismiss()
                    onCancel()
                })
            }
        })
}
