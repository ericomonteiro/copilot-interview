package com.github.ericomonteiro.pirateparrotai.ui.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.pirateparrotai.ai.SolutionResponse
import com.github.ericomonteiro.pirateparrotai.ui.components.CodeDisplay
import com.github.ericomonteiro.pirateparrotai.ui.settings.AVAILABLE_LANGUAGES
import com.github.ericomonteiro.pirateparrotai.ui.theme.AppColors
import com.github.ericomonteiro.pirateparrotai.util.ClipboardUtils
import org.koin.compose.koinInject

@Composable
fun ScreenshotAnalysisScreen(
    autoCapture: Boolean = false,
    onSettingsClick: () -> Unit = {},
    onCertificationClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onAutoCaptureConsumed: () -> Unit = {}
) {
    val viewModel: ScreenshotAnalysisViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    
    // Auto-trigger capture when requested (e.g., from keyboard shortcut)
    LaunchedEffect(autoCapture) {
        if (autoCapture && state.screenshotBase64 == null && !state.isCapturing) {
            viewModel.captureAndAnalyze()
            onAutoCaptureConsumed()
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "🦜",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Code Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Recapture button (shown when there's a screenshot)
                if (state.screenshotBase64 != null) {
                    FilledTonalIconButton(
                        onClick = { viewModel.captureAndAnalyze() },
                        enabled = !state.isCapturing && !state.isLoading
                    ) {
                        Icon(Icons.Outlined.CameraAlt, "Recapture Screenshot")
                    }
                }
                
                // Language selector
                var expanded by remember { mutableStateOf(false) }
                Box {
                    FilledTonalButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(state.selectedLanguage)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        AVAILABLE_LANGUAGES.forEach { lang ->
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
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Certification button
                IconButton(onClick = onCertificationClick) {
                    Icon(Icons.Outlined.School, "AWS Certification")
                }
                
                // Home button
                IconButton(onClick = onHomeClick) {
                    Icon(Icons.Outlined.Home, "Home")
                }
                
                // Settings button
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Outlined.Settings, "Settings")
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
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            AppColors.Primary.copy(alpha = 0.3f),
                                            AppColors.Primary.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Code,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = AppColors.Primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "Capture Screenshot",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Click the button below to capture your screen and analyze the coding challenge.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { viewModel.captureAndAnalyze() },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Primary
                            )
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture & Analyze", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "Shortcut: Cmd+Shift+Opt+S",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                    ClipboardUtils.copyToClipboard(state.error ?: "")
                                }
                            ) {
                                Text("Copy Error")
                            }
                        }
                    }
                }
            }
            state.solution != null -> {
                SolutionContent(solution = state.solution!!, language = state.selectedLanguage)
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
fun SolutionContent(solution: SolutionResponse, language: String = "Kotlin") {
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
            0 -> CodeTab(code = solution.code, language = language)
            1 -> ExplanationTab(
                explanation = solution.explanation,
                timeComplexity = solution.timeComplexity,
                spaceComplexity = solution.spaceComplexity
            )
        }
    }
}

@Composable
fun CodeTab(code: String, language: String = "Kotlin") {
    var copied by remember { mutableStateOf(false) }
    
    // Auto-copy to clipboard when code is displayed
    LaunchedEffect(code) {
        ClipboardUtils.copyToClipboard(code)
        copied = true
        kotlinx.coroutines.delay(3000)
        copied = false
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (copied) {
                Text(
                    "✓ Copied to clipboard!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = { 
                    ClipboardUtils.copyToClipboard(code)
                    copied = true
                }
            ) {
                Text("Copy Code")
            }
        }
        
        CodeDisplay(
            code = code,
            language = language,
            showLineNumbers = true
        )
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

