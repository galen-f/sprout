package com.example.sprout.ui.plantdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sprout.domain.model.CareEventType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogCareEventSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onLogWatering: () -> Unit,
    onLogFertilizing: () -> Unit,
    onLogEvent: (CareEventType, String?) -> Unit,
) {
    var selectedType by remember { mutableStateOf(CareEventType.WATERED) }
    var noteText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp),
        ) {
            Text("Log care event", modifier = Modifier.padding(bottom = 16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CareEventType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.label()) },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (selectedType == CareEventType.NOTE || selectedType == CareEventType.PH_MEASURED) {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    when (selectedType) {
                        CareEventType.WATERED -> onLogWatering()
                        CareEventType.FERTILIZED -> onLogFertilizing()
                        else -> onLogEvent(selectedType, noteText.trim().ifBlank { null })
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Log")
            }
        }
    }
}

private fun CareEventType.label(): String = when (this) {
    CareEventType.WATERED -> "Watered"
    CareEventType.FERTILIZED -> "Fertilized"
    CareEventType.PH_MEASURED -> "pH"
    CareEventType.REPOTTED -> "Repotted"
    CareEventType.NOTE -> "Note"
}
