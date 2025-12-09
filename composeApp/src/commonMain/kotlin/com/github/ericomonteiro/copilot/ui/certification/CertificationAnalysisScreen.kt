package com.github.ericomonteiro.copilot.ui.certification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.copilot.ai.CertificationQuestionAnswer
import com.github.ericomonteiro.copilot.ai.CertificationResponse
import com.github.ericomonteiro.copilot.ai.CertificationType
import org.koin.compose.koinInject

@Composable
fun CertificationAnalysisScreen(
    autoCapture: Boolean = false,
    onSettingsClick: () -> Unit = {},
    onCodeChallengeClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onAutoCaptureConsumed: () -> Unit = {}
) {
    val viewModel: CertificationAnalysisViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    
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
            Text(
                text = "AWS Certification Assistant",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            if (state.screenshotBase64 != null) {
                IconButton(
                    onClick = { viewModel.captureAndAnalyze() },
                    enabled = !state.isCapturing && !state.isLoading
                ) {
                    Icon(Icons.Default.CameraAlt, "Recapture Screenshot")
                }
            }
            
            // Certification selector
            var expanded by remember { mutableStateOf(false) }
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(state.selectedCertification.displayName.replace("AWS ", ""))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CertificationType.entries.forEach { cert ->
                        DropdownMenuItem(
                            text = { 
                                Column {
                                    Text(cert.displayName)
                                    Text(
                                        cert.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                viewModel.selectCertification(cert)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Code Challenge button
            IconButton(onClick = onCodeChallengeClick) {
                Icon(Icons.Default.Code, "Code Challenges")
            }
            
            // Home button
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Default.Home, "Home")
            }
            
            // Settings button
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Settings")
            }
        }
        
        HorizontalDivider()
        
        // Content
        when {
            state.screenshotBase64 == null && !state.isCapturing && state.error == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "AWS Certification Helper",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            "Capture a screenshot of your certification exam question to get the correct answer with detailed explanations.",
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
                        Text("Analyzing certification question...")
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
            state.response != null -> {
                CertificationAnswerContent(response = state.response!!)
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
fun CertificationAnswerContent(response: CertificationResponse) {
    var copied by remember { mutableStateOf(false) }
    
    // Auto-copy all correct answers to clipboard
    val allAnswers = response.answers.joinToString("\n") { 
        "Q${it.questionNumber}: ${it.correctAnswer}" 
    }
    
    LaunchedEffect(allAnswers) {
        copyToClipboard(allAnswers)
        copied = true
        kotlinx.coroutines.delay(3000)
        copied = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with copy status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📋 ${response.answers.size} Question(s) Answered",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (copied) {
                Text(
                    "✓ Copied!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display each question answer
        response.answers.forEachIndexed { index, answer ->
            QuestionAnswerCard(
                answer = answer,
                onCopyClick = { copyToClipboard(answer.correctAnswer) }
            )
            
            if (index < response.answers.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Exam Tips Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "💡 Exam Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    response.examTips,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Copy all button
        Button(
            onClick = { 
                copyToClipboard(allAnswers)
                copied = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Copy All Answers")
        }
    }
}

@Composable
fun QuestionAnswerCard(
    answer: CertificationQuestionAnswer,
    onCopyClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Question header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Question ${answer.questionNumber}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onCopyClick) {
                    Text("Copy", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            // Question summary
            Text(
                answer.questionSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Correct Answer - highlighted
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "✓ Correct Answer",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        answer.correctAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Expandable details
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(if (expanded) "Hide Details ▲" else "Show Details ▼")
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Explanation
                Text(
                    "Why this is correct:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    answer.explanation,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Incorrect answers explanation
                Text(
                    "Why others are incorrect:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    answer.incorrectAnswersExplanation,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Related services
                if (answer.relatedServices.isNotEmpty()) {
                    Text(
                        "Related Services:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        answer.relatedServices.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
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
