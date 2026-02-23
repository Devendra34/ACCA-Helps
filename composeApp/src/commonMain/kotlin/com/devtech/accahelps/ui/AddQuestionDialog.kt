package com.devtech.accahelps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source

@Composable
fun AddQuestionDialog(
    section: Section,
    selectedSource: Source,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var rangeInput by remember { mutableStateOf("") } // "1-20"
    var chInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Questions to ${section.name} from ${selectedSource.label}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Source Selector
//                SourceDropdown(selectedSource) { selectedSource = it }


                // Study Hub specific fields
                if (selectedSource == Source.StudyHub) {
                    OutlinedTextField(value = typeInput, onValueChange = { typeInput = it }, label = { Text("Type (e.g. OT)") })
                    OutlinedTextField(value = chInput, onValueChange = { chInput = it }, label = { Text("Chapter") })
                }

                OutlinedTextField(
                    value = rangeInput,
                    onValueChange = { rangeInput = it },
                    label = { Text("Question Range (e.g. 1-20)") },
                    placeholder = { Text("1-5, 10, or just 5") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(rangeInput, typeInput, chInput)
                onDismiss()
            }) { Text("Add") }
        }
    )
}
