package com.github.ericomonteiro.copilot.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCodeChallengeClick: () -> Unit,
    onCertificationClick: () -> Unit,
    onGenericExamClick: () -> Unit = {},
    onSettingsClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Interview Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row {
                IconButton(onClick = onCodeChallengeClick) {
                    Icon(Icons.Default.Code, "Code Challenge")
                }
                IconButton(onClick = onCertificationClick) {
                    Icon(Icons.Default.School, "AWS Certification")
                }
                IconButton(onClick = onGenericExamClick) {
                    Icon(Icons.Default.Quiz, "Generic Exam")
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, "Settings")
                }
            }
        }
        
        HorizontalDivider()
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Interview Assistant",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                "Your AI-powered coding companion",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // What is section
            SectionCard(
                icon = Icons.Default.Info,
                title = "What is Interview Assistant?",
                content = """
                    Interview Assistant is a desktop application designed to help you during coding interviews and AWS certification exams.
                    
                    It captures your screen, analyzes the content using Google's Gemini AI, and provides:
                    
                    • Code solutions with explanations for coding challenges
                    • Correct answers with detailed explanations for certification questions
                    • Time and space complexity analysis
                    • Exam tips and related AWS services
                    
                    The app runs in "stealth mode" - it's invisible to screen sharing and recording software.
                """.trimIndent()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Configuration section
            SectionCard(
                icon = Icons.Default.Settings,
                title = "What You Need to Configure",
                content = """
                    Before using the app, you need to set up:
                    
                    1. Gemini API Key (Required)
                       • Go to Google AI Studio (aistudio.google.com)
                       • Create a free API key
                       • Paste it in Settings -> API Key
                    
                    2. Select AI Model (Optional)
                       • Default: gemini-2.5-flash (recommended)
                       • For complex problems: gemini-2.5-pro
                    
                    3. Default Language (Optional)
                       • Choose your preferred programming language
                    
                    4. Stealth Mode (Enabled by default)
                       • Hides app from screen capture
                """.trimIndent(),
                actionButton = {
                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Settings")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // How to use section
            SectionCard(
                icon = Icons.Default.PlayArrow,
                title = "How to Use",
                content = """
                    The app has two main modes:
                    
                    Code Challenges
                    • Navigate to a coding problem (LeetCode, HackerRank, etc.)
                    • Press Cmd+Shift+Opt+S or click "Capture"
                    • The AI will analyze and provide a solution
                    
                    AWS Certification
                    • Open your certification exam question
                    • Select the certification type
                    • Press Cmd+Shift+Opt+S or click "Capture"
                    • Get the correct answer with explanations
                    
                    Keyboard Shortcuts
                    • Cmd+Shift+Opt+S -> Capture & Analyze
                    • Cmd+Shift+Opt+B -> Toggle Stealth Mode
                    
                    Tips
                    • Make sure the question is fully visible
                    • Check Screenshot History for troubleshooting
                """.trimIndent()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick actions
            Text(
                "Get Started",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Code,
                    title = "Code Challenge",
                    description = "Solve coding problems",
                    onClick = onCodeChallengeClick
                )
                
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.School,
                    title = "Certification",
                    description = "AWS exam questions",
                    onClick = onCertificationClick
                )
                
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Quiz,
                    title = "Generic Exam",
                    description = "ENEM, Vestibular, Concursos",
                    onClick = onGenericExamClick
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    actionButton: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(16.dp))
                actionButton()
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}
