package com.archer.github.app.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.archer.github.app.logic.model.DropDownMenuList

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDropDownMenu(
    modifier: Modifier = Modifier,
    dropDownMenuList: DropDownMenuList,
    onResult: (label: String) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(dropDownMenuList.options.first()) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOption.value,
            onValueChange = {},
            trailingIcon = {
                TrailingIcon(
                    expanded = expanded
                )
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            ),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            dropDownMenuList.options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption.value = option
                        expanded = false
                        onResult(option)
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}
