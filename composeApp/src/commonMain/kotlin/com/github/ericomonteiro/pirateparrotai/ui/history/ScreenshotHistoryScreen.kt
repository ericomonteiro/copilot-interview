package com.github.ericomonteiro.pirateparrotai.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.pirateparrotai.data.repository.ScreenshotType
import com.github.ericomonteiro.pirateparrotai.db.ScreenshotHistory
import org.jetbrains.skia.Image
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScreenshotHistoryScreen(
    onBackClick: () -> Unit = {}
) {
    val viewModel: ScreenshotHistoryViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    
    var showClearDialog by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Text(
                    text = "Screenshot History",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Row {
                IconButton(onClick = { viewModel.loadHistory() }) {
                    Icon(Icons.Default.Refresh, "Refresh")
                }
                IconButton(onClick = { showClearDialog = true }) {
                    Icon(Icons.Default.Delete, "Clear All")
                }
            }
        }
        
        HorizontalDivider()
        
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.screenshots.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No screenshots yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Screenshots will appear here after you capture and analyze them",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                // List
                LazyColumn(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                ) {
                    items(state.screenshots) { screenshot ->
                        ScreenshotListItem(
                            screenshot = screenshot,
                            isSelected = state.selectedScreenshot?.id == screenshot.id,
                            onClick = { viewModel.selectScreenshot(screenshot) },
                            onDelete = { viewModel.deleteScreenshot(screenshot.id) }
                        )
                    }
                }
                
                VerticalDivider()
                
                // Detail view
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                ) {
                    state.selectedScreenshot?.let { screenshot ->
                        ScreenshotDetailView(screenshot = screenshot)
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Select a screenshot to view details",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Clear confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("This will permanently delete all screenshot history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScreenshotListItem(
    screenshot: ScreenshotHistory,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val typeIcon = if (screenshot.type == ScreenshotType.CODE_CHALLENGE.name) {
        Icons.Default.Code
    } else {
        Icons.Default.School
    }
    val hasError = screenshot.error != null
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                typeIcon,
                contentDescription = null,
                tint = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (screenshot.type == ScreenshotType.CODE_CHALLENGE.name) 
                        "Code Challenge" 
                    else 
                        "Certification",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(Date(screenshot.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (hasError) {
                    Text(
                        text = "⚠ Error",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ScreenshotDetailView(screenshot: ScreenshotHistory) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    var showImage by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Type: ${if (screenshot.type == ScreenshotType.CODE_CHALLENGE.name) "Code Challenge" else "Certification"}",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        dateFormat.format(Date(screenshot.timestamp)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                screenshot.metadata?.let { metadata ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Metadata: $metadata",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Screenshot preview toggle
        OutlinedButton(
            onClick = { showImage = !showImage },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                if (showImage) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (showImage) "Hide Screenshot" else "Show Screenshot")
        }
        
        if (showImage) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                val imageBitmap = remember(screenshot.screenshotBase64) {
                    try {
                        val bytes = java.util.Base64.getDecoder().decode(screenshot.screenshotBase64)
                        Image.makeFromEncoded(bytes).toComposeImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
                
                imageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Screenshot",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    )
                } ?: Text(
                    "Failed to load image",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error section
        screenshot.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⚠ Error",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Analysis result
        screenshot.analysisResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "✓ Analysis Result",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        result,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 50,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Copy buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { copyToClipboard(screenshot.screenshotBase64) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Copy Base64")
            }
            screenshot.analysisResult?.let { result ->
                Button(
                    onClick = { copyToClipboard(result) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy Result")
                }
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
