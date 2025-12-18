package com.github.ericomonteiro.pirateparrotai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.ericomonteiro.pirateparrotai.ui.theme.AppColors
import com.github.ericomonteiro.pirateparrotai.i18n.strings

@Composable
fun HomeScreen(
    onCodeChallengeClick: () -> Unit,
    onCertificationClick: () -> Unit,
    onGenericExamClick: () -> Unit = {},
    onSettingsClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val strings = strings()
    
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
                Text(
                    "🦜",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    strings.appName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            }
            
            Row {
                IconButton(onClick = onCodeChallengeClick) {
                    Icon(Icons.Default.Code, strings.homeCodeChallenge)
                }
                IconButton(onClick = onCertificationClick) {
                    Icon(Icons.Default.School, strings.homeCertification)
                }
                IconButton(onClick = onGenericExamClick) {
                    Icon(Icons.Default.Quiz, strings.homeGenericExam)
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, strings.settings)
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
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.2f),
                                AppColors.Secondary.copy(alpha = 0.15f),
                                AppColors.Tertiary.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🦜",
                        style = MaterialTheme.typography.displayLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        strings.appName,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    
                    Text(
                        strings.homeYourAiCompanion,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // What is section
            SectionCard(
                icon = Icons.Outlined.Info,
                title = strings.homeWhatIsTitle,
                content = strings.homeWhatIsContent
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Configuration section
            SectionCard(
                icon = Icons.Outlined.Settings,
                title = strings.homeConfigTitle,
                content = strings.homeConfigContent,
                actionButton = {
                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.homeOpenSettings)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // How to use section
            SectionCard(
                icon = Icons.Outlined.PlayArrow,
                title = strings.homeHowToUseTitle,
                content = strings.homeHowToUseContent
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick actions
            Text(
                strings.homeGetStarted,
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
                    icon = Icons.Outlined.Code,
                    title = strings.homeCodeChallenge,
                    description = strings.homeCodeChallengeDesc,
                    gradientColors = listOf(AppColors.Primary, AppColors.PrimaryDark),
                    onClick = onCodeChallengeClick
                )
                
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.School,
                    title = strings.homeCertification,
                    description = strings.homeCertificationDesc,
                    gradientColors = listOf(AppColors.Secondary, AppColors.SecondaryDark),
                    onClick = onCertificationClick
                )
                
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Quiz,
                    title = strings.homeGenericExam,
                    description = strings.homeGenericExamDesc,
                    gradientColors = listOf(AppColors.Tertiary, AppColors.TertiaryDark),
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
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
    gradientColors: List<androidx.compose.ui.graphics.Color> = listOf(AppColors.Primary, AppColors.PrimaryDark),
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.3f) }
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(gradientColors.first().copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = gradientColors.first()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
