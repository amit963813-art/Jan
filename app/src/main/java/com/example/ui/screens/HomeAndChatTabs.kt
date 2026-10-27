package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ALL_14_TOOL_CATEGORIES
import com.example.model.ActiveMission
import com.example.model.ChatBubbleStyle
import com.example.model.ChatMessage
import com.example.model.KayraConfig
import com.example.model.KayraTab
import com.example.viewmodel.KayraViewModel
import java.util.Locale

@Composable
fun KayraTopHeader(
    config: KayraConfig,
    timerSeconds: Int,
    onSelectModel: (String) -> Unit,
    onToggleMute: () -> Unit,
    onTriggerVoiceInput: () -> Unit,
    onLockApp: () -> Unit,
    onOpenCustomizeStudio: () -> Unit,
    onCancelTimer: () -> Unit
) {
    var showModelMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // AI Name + Model Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Emblem",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = config.aiName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .clickable { showModelMenu = true }
                                    .testTag("model_selector_chip")
                            ) {
                                Text(
                                    text = "${config.selectedModel} ▾",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showModelMenu,
                                onDismissRequest = { showModelMenu = false }
                            ) {
                                listOf(
                                    "gemini-3.8-flash" to "gemini-3.8-flash (Fast Default)",
                                    "gemini-4-argon" to "gemini-4-argon (Pro Reasoning)",
                                    "gemini-3.5-flash-lite" to "gemini-3.5-flash-lite (Ultra Lite)"
                                ).forEach { (modelId, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            onSelectModel(modelId)
                                            showModelMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Live Timer Badge (if running)
                if (timerSeconds > 0) {
                    val mins = timerSeconds / 60
                    val secs = timerSeconds % 60
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { onCancelTimer() }
                            .testTag("header_live_timer_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Live Timer",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Header Action Buttons: Voice ON/Mute, Voice Control, Safe PIN Lock, Edit UI
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.testTag("header_voice_mute_button")
                    ) {
                        Icon(
                            imageVector = if (config.isVoiceMuted) {
                                Icons.AutoMirrored.Filled.VolumeOff
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = if (config.isVoiceMuted) "Unmute Voice" else "Mute Voice",
                            tint = if (config.isVoiceMuted) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            }
                        )
                    }
                    IconButton(
                        onClick = onTriggerVoiceInput,
                        modifier = Modifier.testTag("header_voice_control_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Control",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onLockApp,
                        modifier = Modifier.testTag("header_pin_lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safe PIN Lock",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        onClick = onOpenCustomizeStudio,
                        modifier = Modifier.testTag("header_customize_ui_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Customize My UI",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KayraBottomBar(
    currentTab: KayraTab,
    onSelectTab: (KayraTab) -> Unit,
    onCenterMicClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTabItem(
                label = "Home",
                icon = Icons.Default.Home,
                selected = currentTab == KayraTab.HOME,
                tag = "nav_tab_home",
                onClick = { onSelectTab(KayraTab.HOME) }
            )
            BottomNavTabItem(
                label = "Chat",
                icon = Icons.Default.Chat,
                selected = currentTab == KayraTab.CHAT,
                tag = "nav_tab_chat",
                onClick = { onSelectTab(KayraTab.CHAT) }
            )

            // Center Floating Glowing Mic Orb Tab
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                    .clickable {
                        onSelectTab(KayraTab.MIC)
                        onCenterMicClick()
                    }
                    .testTag("nav_tab_center_mic_orb"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Center Mic Orb",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            BottomNavTabItem(
                label = "Tools (14)",
                icon = Icons.Default.Bolt,
                selected = currentTab == KayraTab.TOOLS,
                tag = "nav_tab_tools",
                onClick = { onSelectTab(KayraTab.TOOLS) }
            )
            BottomNavTabItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                selected = currentTab == KayraTab.SETTINGS,
                tag = "nav_tab_settings",
                onClick = { onSelectTab(KayraTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun BottomNavTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            fontSize = 11.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeTabContent(
    config: KayraConfig,
    unreadNotificationCount: Int,
    activeMission: ActiveMission?,
    onReplayGreeting: () -> Unit,
    onStartVoiceOrb: () -> Unit,
    onOpenNotificationsTool: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    onOpenToolCategoryId: (Int) -> Unit,
    onPauseResumeMission: () -> Unit,
    onCancelMission: () -> Unit
) {
    var quickInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting Card + Notification Bell + Replay Voice Button
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${config.userName}",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("home_greeting_title")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "How can I assist you today?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${config.customGreeting}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Notification Bell with unread count badge
                    IconButton(
                        onClick = onOpenNotificationsTool,
                        modifier = Modifier.testTag("home_notification_bell")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text(unreadNotificationCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onReplayGreeting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("replay_greeting_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Replay Voice Greeting",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🔊 Replay Voice Greeting")
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Wake: \"${config.wakeWord}\"",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Active Autonomous Mission Bar (if running)
        if (activeMission != null) {
            Spacer(modifier = Modifier.height(12.dp))
            ActiveMissionCard(
                mission = activeMission,
                onPauseResume = onPauseResumeMission,
                onCancel = onCancelMission
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Large Animated Glowing Neon Purple/Pink Center Microphone Orb
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val accentCyan = MaterialTheme.colorScheme.tertiary

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .clickable { onStartVoiceOrb() }
                .testTag("home_center_mic_orb")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = this.center
                val baseRadius = size.minDimension / 2.2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.42f),
                            secondaryColor.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * pulseScale
                    ),
                    radius = baseRadius * pulseScale
                )
                drawCircle(
                    color = accentCyan.copy(alpha = 0.55f),
                    radius = baseRadius * 0.78f * pulseScale,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.85f),
                    radius = baseRadius * 0.62f,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        )
                    )
                    .border(2.dp, accentCyan, CircleShape)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Tap to Speak to Kayra",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "TAP ORB",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "Tap Orb to Speak in Hindi, Hinglish, or English",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5 Feature Badges
        val badges = listOf(
            "🎙️ Voice Assistant" to 3,
            "🤖 Screen Automation" to 12,
            "⚡ AI Powered" to 6,
            "🛡️ Privacy First" to 14,
            "🌐 Multi-Language" to 1
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            badges.forEach { (badgeTitle, toolId) ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onOpenToolCategoryId(toolId) }
                ) {
                    Text(
                        text = badgeTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Voice Command Chips
        Text(
            text = "QUICK VOICE & AI COMMANDS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        val quickChips = listOf(
            "Namaste Kayra! Aaj ka motivational plan batao",
            "Mera secret note save karo: Project launch Friday 6 PM",
            "Start timer 30 seconds",
            "Save parking spot",
            "Emergency SOS & Live GPS",
            "Lock"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickChips.forEach { cmd ->
                AssistChip(
                    onClick = {
                        if (cmd.contains("Emergency SOS")) {
                            onOpenToolCategoryId(1)
                        } else {
                            onQuickPrompt(cmd)
                        }
                    },
                    label = { Text(cmd) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick 14 Tool Grid Preview Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "14 INTERACTIVE TOOL HUB",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tap any tool to launch",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ALL_14_TOOL_CATEGORIES.take(6).forEach { cat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .width(175.dp)
                        .clickable { onOpenToolCategoryId(cat.id) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = cat.badge,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = cat.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Quick Chat Input Bar on Home Screen
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = quickInput,
                onValueChange = { quickInput = it },
                placeholder = { Text("Ask ${config.aiName} or type 'Lock'...") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_chat_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (quickInput.isNotBlank()) {
                        onQuickPrompt(quickInput)
                        quickInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("home_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ActiveMissionCard(
    mission: ActiveMission,
    onPauseResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (mission.isCompleted) "MISSION COMPLETED" else "AUTONOMOUS MISSION ACTIVE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!mission.isCompleted) {
                        OutlinedButton(
                            onClick = onPauseResume,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (mission.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Pause/Resume",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (mission.isPaused) "Resume" else "Pause")
                        }
                    }
                    OutlinedButton(
                        onClick = onCancel,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (mission.isCompleted) "Dismiss" else "Cancel")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mission.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            val progress = (mission.currentStepIndex + 1).toFloat() / mission.steps.size.coerceAtLeast(1)
            LinearProgressIndicator(
                progress = { progress },
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = mission.steps.getOrElse(mission.currentStepIndex) { "" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (mission.aiLog.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mission.aiLog,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatTabContent(
    viewModel: KayraViewModel,
    config: KayraConfig,
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    onStartVoiceInput: () -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val listState = rememberLazyListState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val src = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                attachedBitmap = bmp
            } catch (e: Exception) {
                // Ignore decode failure
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            attachedBitmap = bmp
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubbleItem(
                    message = msg,
                    bubbleStyle = config.chatBubbleStyle,
                    aiName = config.aiName,
                    userName = config.userName,
                    onCopy = { viewModel.voiceManager.copyToClipboard("Kayra AI Message", msg.text) },
                    onSpeak = { viewModel.speakText(msg.text) }
                )
            }
        }

        if (attachedBitmap != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            bitmap = attachedBitmap!!.asImageBitmap(),
                            contentDescription = "Attached Vision Image",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Image attached for Gemini Vision / OCR",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    OutlinedButton(onClick = { attachedBitmap = null }) {
                        Text("Remove")
                    }
                }
            }
        }

        // Chat Input Bar with Vision Upload, Camera, Mic, and Send
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("chat_pick_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Upload Photo for Vision/OCR",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.testTag("chat_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture Photo for Vision/OCR",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = onStartVoiceInput,
                    modifier = Modifier.testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak Message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Chat with ${config.aiName} (${config.selectedModel})...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input")
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() || attachedBitmap != null) {
                            viewModel.processCommandOrChat(inputText, attachedBitmap)
                            inputText = ""
                            attachedBitmap = null
                        }
                    },
                    enabled = !isGenerating,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("chat_send_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    bubbleStyle: ChatBubbleStyle,
    aiName: String,
    userName: String,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val shape = when (bubbleStyle) {
        ChatBubbleStyle.ROUNDED -> RoundedCornerShape(24.dp)
        ChatBubbleStyle.GLASSMORPHIC -> RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (message.isUser) 18.dp else 4.dp,
            bottomEnd = if (message.isUser) 4.dp else 18.dp
        )
        ChatBubbleStyle.MINIMAL -> RoundedCornerShape(8.dp)
    }

    val containerColor = when {
        message.isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
        message.isUser -> MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
        bubbleStyle == ChatBubbleStyle.MINIMAL -> Color.Transparent
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    }

    val borderStroke = when {
        message.isError -> BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        message.isUser -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = shape,
            color = containerColor,
            border = borderStroke,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.isUser) userName else aiName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                if (message.attachedBitmap != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = message.attachedBitmap.asImageBitmap(),
                        contentDescription = "Attached Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (message.text.isEmpty() && message.isStreaming) "Synthesizing response..." else message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (message.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )

                if (!message.isUser && message.text.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onCopy,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy response",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = onSpeak,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak response",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speak", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
