package com.example.witube.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.witube.presentation.viewmodel.AudioState

@Composable
fun FirstScreen(
    audioState: AudioState,
    onConvertClick: (String) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var url by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Witube",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = url,
            onValueChange = { 
                url = it
                inputError = null
            },
            label = { Text("URL de YouTube") },
            placeholder = { Text("https://www.youtube.com/watch?v=...") },
            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
            isError = inputError != null,
            supportingText = { inputError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (url.isNotBlank()) onConvertClick(url) else inputError = "La URL no puede estar vacía"
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (url.isNotBlank()) {
                    onConvertClick(url)
                } else {
                    inputError = "La URL no puede estar vacía"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = audioState !is AudioState.Loading
        ) {
            if (audioState is AudioState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Obteniendo información...")
            } else {
                Text("Convertir", style = MaterialTheme.typography.titleMedium)
            }
        }

        if (audioState is AudioState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = audioState.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onErrorDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text("Aceptar", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}
