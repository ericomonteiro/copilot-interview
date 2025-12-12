package com.github.ericomonteiro.pirateparrotai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.pirateparrotai.screenshot.CaptureRegion
import com.github.ericomonteiro.pirateparrotai.ui.theme.AppColors
import com.github.ericomonteiro.pirateparrotai.util.ClipboardUtils
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onCloseClick: () -> Unit,
    onHideFromCaptureChanged: (Boolean) -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSelectCaptureRegion: () -> Unit = {},
    viewModel: SettingsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current
    
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "🦜",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            FilledTonalIconButton(onClick = onCloseClick) {
                Icon(Icons.Outlined.Close, "Close")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // API Key Section
        SettingsSectionHeader(icon = Icons.Outlined.Key, title = "Gemini API Key")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.apiKey,
            onValueChange = { viewModel.setApiKey(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("sk-...") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        val apiKeyUrl = "https://aistudio.google.com/app/apikey"
        val apiKeyAnnotatedString = buildAnnotatedString {
            append("Get your API key from ")
            pushStringAnnotation(tag = "URL", annotation = apiKeyUrl)
            withStyle(style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )) {
                append("Google AI Studio")
            }
            pop()
        }
        ClickableText(
            text = apiKeyAnnotatedString,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(top = 4.dp),
            onClick = { offset ->
                apiKeyAnnotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Model Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsSectionHeader(icon = Icons.Outlined.SmartToy, title = "AI Model")
            FilledTonalButton(
                onClick = { viewModel.loadAvailableModels() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reload")
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
        SettingsSectionHeader(icon = Icons.Outlined.Language, title = "Default Language")
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
        SettingsSectionHeader(icon = Icons.Outlined.VisibilityOff, title = "Stealth Features")
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
        
        // Capture Region Section
        SettingsSectionHeader(icon = Icons.Outlined.Crop, title = "Capture Region")
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Use Custom Region")
                Text(
                    if (state.captureRegionEnabled && state.captureRegion != null) {
                        "Region: ${state.captureRegion!!.width} x ${state.captureRegion!!.height} at (${state.captureRegion!!.x}, ${state.captureRegion!!.y})"
                    } else {
                        "Capture entire screen"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = state.captureRegionEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && state.captureRegion == null) {
                        onSelectCaptureRegion()
                    } else {
                        viewModel.setCaptureRegionEnabled(enabled)
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onSelectCaptureRegion,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.SelectAll, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Select Region")
            }
            
            if (state.captureRegionEnabled) {
                OutlinedButton(
                    onClick = { viewModel.clearCaptureRegion() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }
        }
        
        Text(
            "Define a specific screen area to capture instead of the entire screen",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Troubleshooting
        SettingsSectionHeader(icon = Icons.Outlined.BugReport, title = "Troubleshooting")
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(
            onClick = onHistoryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Screenshot History")
        }
        Text(
            "View captured screenshots and analysis results for debugging",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Test API Button
        Button(
            onClick = { viewModel.testApiConnection() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Icon(Icons.Outlined.NetworkCheck, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Test API Connection", fontWeight = FontWeight.Bold)
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
                        onClick = { ClipboardUtils.copyToClipboard(result) },
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
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Primary.copy(alpha = 0.1f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
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

@Composable
private fun SettingsSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.Primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

