package com.github.ericomonteiro.copilot.ui.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.copilot.ai.SolutionResponse
import org.koin.compose.koinInject

@Composable
fun ScreenshotAnalysisScreen(
    autoCapture: Boolean = false,
    onBackClick: () -> Unit
) {
    val viewModel: ScreenshotAnalysisViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    
    // Auto-trigger capture when requested (e.g., from keyboard shortcut)
    LaunchedEffect(autoCapture) {
        if (autoCapture && state.screenshotBase64 == null && !state.isCapturing) {
            viewModel.captureAndAnalyze()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            
            Text(
                text = "Screenshot Analysis",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            
            // Recapture button (shown when there's a screenshot)
            if (state.screenshotBase64 != null) {
                IconButton(
                    onClick = { viewModel.captureAndAnalyze() },
                    enabled = !state.isCapturing && !state.isLoading
                ) {
                    Icon(Icons.Default.CameraAlt, "Recapture Screenshot")
                }
            }
            
            // Language selector
            var expanded by remember { mutableStateOf(false) }
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(state.selectedLanguage)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Kotlin", "Java", "Python", "JavaScript", "C++", "Go", "Rust").forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                viewModel.selectLanguage(lang)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        HorizontalDivider()
        
        // Content
        when {
            state.screenshotBase64 == null && !state.isCapturing && state.error == null -> {
                // Initial state - show capture button
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "Capture Screenshot",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            "Click the button below to capture your screen and analyze the coding challenge.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        Button(
                            onClick = { viewModel.captureAndAnalyze() },
                            modifier = Modifier.size(width = 200.dp, height = 56.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture & Analyze")
                        }
                    }
                }
            }
            state.isCapturing -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Capturing screenshot...")
                    }
                }
            }
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing screenshot and generating solution...")
                    }
                }
            }
            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry")
                            }
                            OutlinedButton(
                                onClick = {
                                    copyToClipboard(state.error ?: "")
                                }
                            ) {
                                Text("Copy Error")
                            }
                        }
                    }
                }
            }
            state.solution != null -> {
                SolutionContent(solution = state.solution!!)
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun SolutionContent(solution: SolutionResponse) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Code") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Explanation") }
            )
        }
        
        when (selectedTab) {
            0 -> CodeTab(code = solution.code)
            1 -> ExplanationTab(
                explanation = solution.explanation,
                timeComplexity = solution.timeComplexity,
                spaceComplexity = solution.spaceComplexity
            )
        }
    }
}

@Composable
fun CodeTab(code: String) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        SelectionContainer {
            Text(
                text = code,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD4D4D4)
                ),
                modifier = Modifier.verticalScroll(scrollState)
            )
        }
    }
}

@Composable
fun ExplanationTab(
    explanation: String,
    timeComplexity: String,
    spaceComplexity: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Explanation",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(explanation)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Complexity Analysis",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Time: $timeComplexity")
        Text("Space: $spaceComplexity")
    }
}

private fun copyToClipboard(text: String) {
    try {
        val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = java.awt.datatransfer.StringSelection(text)
        clipboard.setContents(stringSelection, null)
    } catch (e: Exception) {
        // Silently fail if clipboard access is not available
        e.printStackTrace()
    }
}
