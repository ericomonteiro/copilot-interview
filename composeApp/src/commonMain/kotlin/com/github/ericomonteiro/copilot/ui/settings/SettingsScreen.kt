package com.github.ericomonteiro.copilot.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onCloseClick: () -> Unit,
    onHideFromCaptureChanged: (Boolean) -> Unit = {},
    viewModel: SettingsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    
    // Update hide from capture in parent when it changes
    LaunchedEffect(state.hideFromCapture) {
        onHideFromCaptureChanged(state.hideFromCapture)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Default.Close, "Close")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // API Key
        Text("Gemini API Key", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.apiKey,
            onValueChange = { viewModel.setApiKey(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("sk-...") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Text(
            "Get your API key from https://makersuite.google.com/app/apikey",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Model Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("AI Model", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { viewModel.loadAvailableModels() }) {
                Text("Reload Models")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        var modelExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { modelExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoadingModels
            ) {
                if (state.isLoadingModels) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val selectedModel = state.availableModels.find { it.id == state.selectedModel }
                            Text(selectedModel?.displayName ?: state.selectedModel)
                            Text(
                                selectedModel?.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
            }
            DropdownMenu(
                expanded = modelExpanded,
                onDismissRequest = { modelExpanded = false }
            ) {
                state.availableModels.forEach { model ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(model.displayName)
                                Text(
                                    model.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            viewModel.setSelectedModel(model.id)
                            modelExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Default Language Selection
        Text("Default Language", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        var languageExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { languageExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.defaultLanguage)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            }
            DropdownMenu(
                expanded = languageExpanded,
                onDismissRequest = { languageExpanded = false }
            ) {
                AVAILABLE_LANGUAGES.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang) },
                        onClick = {
                            viewModel.setDefaultLanguage(lang)
                            languageExpanded = false
                        }
                    )
                }
            }
        }
        Text(
            "This language will be pre-selected when analyzing challenges",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stealth Mode
        Text("Stealth Features", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hide from Screen Capture")
                Text(
                    "Makes window invisible in Zoom, Meet, Teams",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = state.hideFromCapture,
                onCheckedChange = { viewModel.setHideFromCapture(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Test API Button
        Button(
            onClick = { viewModel.testApiConnection() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test API Connection & List Models")
        }
        
        // Show test result
        state.testResult?.let { result ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.startsWith("Error")) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { copyToClipboard(result) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Copy Result")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Tips",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Global Hotkeys (work even when app is not focused):\n" +
                    "• Cmd+Shift+Opt+S - Capture screenshot & analyze\n" +
                    "• Cmd+Shift+Opt+B - Toggle stealth mode\n\n" +
                    "• Solution code is auto-copied to clipboard!\n" +
                    "• Gemini is completely FREE (no credit card needed)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun copyToClipboard(text: String) {
    try {
        val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = java.awt.datatransfer.StringSelection(text)
        clipboard.setContents(stringSelection, null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
