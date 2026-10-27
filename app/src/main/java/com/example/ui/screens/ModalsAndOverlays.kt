package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ChatBubbleStyle
import com.example.model.KayraConfig
import com.example.model.ToolCategorySpec
import com.example.ui.theme.NeonThemePreset
import com.example.viewmodel.KayraViewModel
import com.example.viewmodel.ToolAiOutputState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomizeUiStudioModal(
    config: KayraConfig,
    onDismiss: () -> Unit,
    onSave: (
        userName: String,
        aiName: String,
        wakeWord: String,
        greeting: String,
        preset: NeonThemePreset,
        isDark: Boolean,
        bubbleStyle: ChatBubbleStyle,
        pinCode: String
    ) -> Unit
) {
    var userName by remember { mutableStateOf(config.userName) }
    var aiName by remember { mutableStateOf(config.aiName) }
    var wakeWord by remember { mutableStateOf(config.wakeWord) }
    var greeting by remember { mutableStateOf(config.customGreeting) }
    var selectedPreset by remember { mutableStateOf(config.themePreset) }
    var isDark by remember { mutableStateOf(config.isDarkMode) }
    var selectedBubble by remember { mutableStateOf(config.chatBubbleStyle) }
    var pinCode by remember { mutableStateOf(config.pinCode) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎨 CUSTOMIZE MY UI STUDIO",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Studio")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("User Name (e.g., Amit)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_user_name_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = aiName,
                onValueChange = { aiName = it },
                label = { Text("AI Companion Name (e.g., KAYRA)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_ai_name_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = wakeWord,
                    onValueChange = { wakeWord = it },
                    label = { Text("Wake Word") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = pinCode,
                    onValueChange = { if (it.length <= 4) pinCode = it.filter { c -> c.isDigit() } },
                    label = { Text("4-Digit PIN") },
                    singleLine = true,
                    modifier = Modifier.width(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = greeting,
                onValueChange = { greeting = it },
                label = { Text("Custom Voice Greeting Line") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "NEON COLOR THEME PRESETS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonThemePreset.entries.forEach { preset ->
                    FilterChip(
                        selected = selectedPreset == preset,
                        onClick = { selectedPreset = preset },
                        label = { Text(preset.displayName) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(preset.primaryNeon)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dark Cyberpunk Mode (Toggle Dark / Light)",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = isDark,
                    onCheckedChange = { isDark = it },
                    modifier = Modifier.testTag("studio_dark_mode_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "CHAT BUBBLE STYLE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChatBubbleStyle.entries.forEach { style ->
                    FilterChip(
                        selected = selectedBubble == style,
                        onClick = { selectedBubble = style },
                        label = { Text(style.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    onSave(userName, aiName, wakeWord, greeting, selectedPreset, isDark, selectedBubble, pinCode)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_save_button")
            ) {
                Text("Apply & Speak Customized Greeting")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ToolCategoryModal(
    category: ToolCategorySpec,
    viewModel: KayraViewModel,
    onDismiss: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onRequestCameraPermission: () -> Unit
) {
    val config by viewModel.config.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val diagnostics by viewModel.diagnostics.collectAsState()
    val toolAiState by viewModel.toolAiState.collectAsState()
    val isSynthPlaying by viewModel.isSynthPlaying.collectAsState()
    val currentSynthTrack by viewModel.currentSynthTrack.collectAsState()
    val isHardwareTorchOn by viewModel.isHardwareTorchOn.collectAsState()
    val liveLat by viewModel.liveLat.collectAsState()
    val liveLng by viewModel.liveLng.collectAsState()
    val gpsLabel by viewModel.gpsSourceLabel.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val triggers by viewModel.automationTriggers.collectAsState()
    val macros by viewModel.macros.collectAsState()
    val activeMission by viewModel.activeMission.collectAsState()

    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            capturedPhoto = bmp
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Tool")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            when (category.id) {
                // 1. Communication Tools
                1 -> {
                    var phone by remember { mutableStateOf("+919876543210") }
                    var message by remember { mutableStateOf("Namaste from ${config.userName} via ${config.aiName}!") }
                    var emailTo by remember { mutableStateOf("team@example.com") }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Recipient Phone (WhatsApp / SMS / SOS)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message Payload") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.voiceManager.launchWhatsApp(phone, message) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("WhatsApp")
                        }
                        Button(
                            onClick = { viewModel.voiceManager.launchSms(phone, message) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Send SMS")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailTo,
                        onValueChange = { emailTo = it },
                        label = { Text("Email Recipient") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.voiceManager.launchEmail(emailTo, "Message from ${config.userName}", message) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Compose Email (mailto:)")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🚨 EMERGENCY SOS (LIVE GPS ALERT)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Fetches live GPS coordinates and dispatches an urgent SOS link on WhatsApp or SMS.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onRequestLocationPermission()
                                        viewModel.refreshGpsLocation { lat, lng ->
                                            val sosText = "🚨 EMERGENCY SOS from ${config.userName}! Need help at GPS: https://maps.google.com/?q=$lat,$lng"
                                            viewModel.voiceManager.launchWhatsApp(phone, sosText)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("SOS WhatsApp")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onRequestLocationPermission()
                                        viewModel.refreshGpsLocation { lat, lng ->
                                            val sosText = "EMERGENCY SOS from ${config.userName}! Live GPS: https://maps.google.com/?q=$lat,$lng"
                                            viewModel.voiceManager.launchSms(phone, sosText)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("SOS SMS")
                                }
                            }
                        }
                    }
                }

                // 2. Call Tools
                2 -> {
                    var dialNumber by remember { mutableStateOf("") }
                    var newName by remember { mutableStateOf("") }
                    var newPhone by remember { mutableStateOf("") }
                    var newRel by remember { mutableStateOf("") }
                    var confirmRejectDialog by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = dialNumber,
                        onValueChange = { dialNumber = it },
                        label = { Text("Phone Number to Dial (tel:...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.voiceManager.launchDialer(dialNumber) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Dialer")
                        }
                        OutlinedButton(
                            onClick = { confirmRejectDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject Call Guard")
                        }
                    }
                    if (confirmRejectDialog) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Safety Rule #14: Confirm before rejecting incoming call and sending busy SMS?")
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            confirmRejectDialog = false
                                            viewModel.speakText("Call rejected safely. Sent busy auto-reply.")
                                        }
                                    ) {
                                        Text("Confirm Reject")
                                    }
                                    OutlinedButton(onClick = { confirmRejectDialog = false }) {
                                        Text("Keep Ringing")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("SAVED CONTACTS VAULT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    contacts.forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${c.name} (${c.relation})", style = MaterialTheme.typography.titleMedium)
                                Text(c.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row {
                                IconButton(onClick = { viewModel.voiceManager.launchDialer(c.phone) }) {
                                    Icon(Icons.Default.Call, contentDescription = "Call Contact", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteContact(c.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Contact")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("New Contact Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { newPhone = it },
                            label = { Text("Phone") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newRel,
                            onValueChange = { newRel = it },
                            label = { Text("Tag") },
                            singleLine = true,
                            modifier = Modifier.width(110.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.addContact(newName, newPhone, newRel)
                            newName = ""
                            newPhone = ""
                            newRel = ""
                        }
                    ) {
                        Text("Save Contact to Local Storage")
                    }
                }

                // 3. Media Tools
                3 -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = "Synth", tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentSynthTrack,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.toggleCyberSynth() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isSynthPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Stop Synth"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isSynthPlaying) "Stop Cyber Synth" else "Play Cyber Synth")
                                }
                                OutlinedButton(onClick = { viewModel.nextCyberSynthTrack() }) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Next Track")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Next")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.voiceManager.openUrl("https://music.youtube.com") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🎵 Launch YouTube Music")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "TTS & SYSTEM MEDIA VOLUME: ${(config.ttsVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Slider(
                        value = config.ttsVolume,
                        onValueChange = { viewModel.updateTtsVolume(it) },
                        valueRange = 0f..1f
                    )
                }

                // 4. Device Tools
                4 -> {
                    var timerInputSec by remember { mutableStateOf("60") }
                    var clipboardText by remember { mutableStateOf(viewModel.voiceManager.readFromClipboard()) }

                    Text("LIVE COUNTDOWN TIMER (DISPLAYS IN TOP HEADER)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = timerInputSec,
                            onValueChange = { timerInputSec = it.filter { c -> c.isDigit() } },
                            label = { Text("Seconds") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val sec = timerInputSec.toIntOrNull() ?: 60
                                viewModel.startCountdownTimer(sec)
                            }
                        ) {
                            Text("Start Timer")
                        }
                        OutlinedButton(onClick = { viewModel.cancelCountdownTimer() }) {
                            Text("Stop")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.toggleHardwareTorch() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = "Torch")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHardwareTorchOn) "Torch ON" else "Hardware Torch")
                        }
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                viewModel.setFullScreenTorch(true)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Full-Screen Torch")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            viewModel.lockAppNow()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Instant 4-Digit PIN Lock Screen")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("LIVE BATTERY & STORAGE ANALYZER", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Battery Level: ${diagnostics.batteryPct}% (${if (diagnostics.isCharging) "Charging ⚡" else "Discharging"})")
                            Text("Internal Storage Free: ${diagnostics.storageFreeGb} / ${diagnostics.storageTotalGb}")
                            Text("RAM Memory Used: ${diagnostics.ramUsedGb} / ${diagnostics.ramTotalGb}")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = clipboardText,
                        onValueChange = { clipboardText = it },
                        label = { Text("Clipboard Manager") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.voiceManager.copyToClipboard("Kayra Clip", clipboardText) }) {
                            Text("Copy to Clipboard")
                        }
                        OutlinedButton(onClick = { clipboardText = viewModel.voiceManager.readFromClipboard() }) {
                            Text("Paste Current")
                        }
                    }
                }

                // 5. Files & Photos (Camera + Gemini Vision OCR)
                5 -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onRequestCameraPermission()
                                cameraCaptureLauncher.launch(null)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capture Photo")
                        }
                        if (capturedPhoto != null) {
                            OutlinedButton(onClick = { capturedPhoto = null }) {
                                Text("Delete Photo")
                            }
                        }
                    }

                    if (capturedPhoto != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Image(
                            bitmap = capturedPhoto!!.asImageBitmap(),
                            contentDescription = "Captured Camera Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.runToolTextGeneration(
                                    taskTitle = "Gemini Vision OCR",
                                    prompt = "Extract all visible text from this image accurately and summarize any key details.",
                                    bitmap = capturedPhoto
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔍 Extract Text (OCR via Gemini Vision)")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            val exportData = viewModel.buildSingleFileHtmlExport()
                            viewModel.voiceManager.shareText("Kayra_Vault_Export.html", exportData)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📦 Share / Export Vault Bundle")
                    }

                    ToolAiResultBox(toolAiState = toolAiState, viewModel = viewModel)
                }

                // 6. Smart / Generative Tools
                6 -> {
                    var promptInput by remember { mutableStateOf("A futuristic neon cyberpunk landing page with dark purple glassmorphic cards") }

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("Enter Prompt for Website Builder, Image Gen, Research, or Game Coach") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.runToolTextGeneration(
                                    "Website Builder",
                                    "Generate a complete, responsive single-file HTML5/CSS/JS website for: $promptInput"
                                )
                            }
                        ) {
                            Text("🌐 Website Builder")
                        }
                        Button(
                            onClick = { viewModel.generateRuntimeImage(promptInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("🎨 AI Image Generator")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.runToolTextGeneration(
                                    "Deep Research",
                                    "Perform a structured deep research analysis with key findings, pros/cons, and action items on: $promptInput"
                                )
                            }
                        ) {
                            Text("📚 Deep Research")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.runToolTextGeneration(
                                    "Game Coach",
                                    "Act as a pro esports & strategy Game Coach. Give pro tips, loadout/meta strategy, and drills for: $promptInput"
                                )
                            }
                        ) {
                            Text("🎮 Game Coach")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("LIVE PHONE HEALTH DIAGNOSTIC", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• CPU Cores: ${diagnostics.cpuCores} Threads Active")
                            Text("• Memory (RAM): ${diagnostics.ramUsedGb} used of ${diagnostics.ramTotalGb}")
                            Text("• Storage Free: ${diagnostics.storageFreeGb} of ${diagnostics.storageTotalGb}")
                            Text("• Battery: ${diagnostics.batteryPct}% (${if (diagnostics.isCharging) "Charging" else "On Battery"})")
                            Text("• Network: ${diagnostics.networkStatus}")
                        }
                    }

                    ToolAiResultBox(toolAiState = toolAiState, viewModel = viewModel)
                }

                // 7. Missions
                7 -> {
                    var missionGoal by remember { mutableStateOf("Plan a 3-day AI startup launch sprint with marketing & tech checklist") }
                    OutlinedTextField(
                        value = missionGoal,
                        onValueChange = { missionGoal = it },
                        label = { Text("Autonomous Mission Objective") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.startMission(missionGoal) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🚀 Start Multi-Step Autonomous Mission")
                    }
                    if (activeMission != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ActiveMissionCard(
                            mission = activeMission!!,
                            onPauseResume = { viewModel.togglePauseMission() },
                            onCancel = { viewModel.cancelMission() }
                        )
                    }
                }

                // 8. Notifications & Safety Rule #14 OTP Reader
                8 -> {
                    var replyDraft by remember { mutableStateOf("On my way! Will join in 5 mins.") }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.readSafeNotificationsAloud() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔊 Read Unread Aloud")
                        }
                        OutlinedButton(
                            onClick = { viewModel.clearNotifications() }
                        ) {
                            Text("Clear All")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.readOtpExplicitlyVerified() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🛡️ Verify & Read OTP Aloud (Safety Rule #14)")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    notifications.forEach { n ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${n.appName} • ${n.sender} (${n.time})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (n.isOtp) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = if (n.isOtp && config.otpPrivacyGuard) "🔒 [OTP Protected by Safety Rule #14 — Tap 'Verify & Read OTP' above]" else n.message,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = replyDraft,
                        onValueChange = { replyDraft = it },
                        label = { Text("Quick Reply to Notification") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.voiceManager.launchWhatsApp("", replyDraft) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Quick Reply via WhatsApp")
                    }
                }

                // 9. Maps & "My World"
                9 -> {
                    var destination by remember { mutableStateOf("Connaught Place, New Delhi") }
                    var parkingNote by remember { mutableStateOf("Basement Level 2, Near Pillar B4") }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("MY LIVE GPS LOCATION", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                            Text(
                                text = String.format(Locale.US, "Lat: %.5f, Lng: %.5f", liveLat, liveLng),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(gpsLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onRequestLocationPermission()
                                        viewModel.refreshGpsLocation()
                                    }
                                ) {
                                    Text("📍 Refresh Live GPS")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onRequestLocationPermission()
                                        viewModel.saveCurrentParkingSpot("Parked Vehicle Spot", parkingNote)
                                    }
                                ) {
                                    Text("🚗 Save Parking Spot")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Navigation Destination") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { viewModel.voiceManager.launchMapsQuery(destination) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = "Navigate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Google Maps Directions")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("1-TAP NEARBY PLACES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ATM Near Me", "Hospital Near Me", "Cafe Near Me", "Petrol Pump Near Me").forEach { place ->
                            AssistChipButton(label = place) {
                                viewModel.voiceManager.launchMapsQuery(place)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("SMART MODES (ACTIVE: ${config.activeSmartMode})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Driving Mode", "Sleep Mode", "Work Mode", "Game Mode").forEach { mode ->
                            FilterChip(
                                selected = config.activeSmartMode == mode,
                                onClick = { viewModel.setSmartMode(mode) },
                                label = { Text(mode) }
                            )
                        }
                    }
                }

                // 10. PC Connect
                10 -> {
                    var ip by remember { mutableStateOf(config.pcIpAddress) }
                    var code by remember { mutableStateOf(config.pcPairingCode) }
                    var statusMsg by remember { mutableStateOf("Ready to pair with KAYRA Desktop Agent") }

                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = { Text("PC Local IP:Port") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Pairing Security Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.updatePcConnection(ip, code)
                            statusMsg = "Paired with PC ($ip) using code $code"
                            viewModel.speakText("PC Link configured for $ip")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💻 Save & Connect PC Bridge")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(statusMsg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                statusMsg = "Dispatched Remote Lock command to $ip"
                                viewModel.speakText("Remote PC lock signal dispatched.")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lock PC")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.voiceManager.shareText("Send File to PC ($ip)", viewModel.buildSingleFileHtmlExport())
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Send File to PC")
                        }
                    }
                }

                // 11. Search, Browser & Apps
                11 -> {
                    var query by remember { mutableStateOf("Latest Gemini AI advancements") }
                    var urlInput by remember { mutableStateOf("https://www.google.com") }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Google Search Query") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.voiceManager.openUrl("https://www.google.com/search?q=${android.net.Uri.encode(query)}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔍 Launch Google Search")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Browser URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.voiceManager.openUrl(urlInput) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🌐 Open in Browser")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("OPEN ANY APP LAUNCHER", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChipButton("WhatsApp") { viewModel.voiceManager.openUrl("https://wa.me/") }
                        AssistChipButton("YouTube") { viewModel.voiceManager.openUrl("https://www.youtube.com") }
                        AssistChipButton("Instagram") { viewModel.voiceManager.openUrl("https://www.instagram.com") }
                        AssistChipButton("Google Maps") { viewModel.voiceManager.launchMapsQuery("India") }
                        AssistChipButton("Phone Dialer") { viewModel.voiceManager.launchDialer("") }
                    }
                }

                // 12. Screen Automation
                12 -> {
                    var newMacro by remember { mutableStateOf("") }
                    Button(
                        onClick = {
                            viewModel.speakText(
                                "Auto Screen Read Active. Current user is ${config.userName}, AI Companion is ${config.aiName}, model is ${config.selectedModel}. You have ${notifications.size} notifications and ${macros.size} screen macros queued."
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Screen")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🔊 Auto Screen Read Aloud")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newMacro,
                        onValueChange = { newMacro = it },
                        label = { Text("Tap / Type / Scroll Macro Builder") },
                        placeholder = { Text("e.g., Tap(Search) -> Type('Hello') -> Scroll(Down)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.addScreenMacro(newMacro)
                            newMacro = ""
                        }
                    ) {
                        Text("Add Macro to Queue")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    macros.forEach { m ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(m, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                OutlinedButton(onClick = { viewModel.speakText("Executing macro: $m") }) {
                                    Text("Run")
                                }
                            }
                        }
                    }
                }

                // 13. Automation & Triggers + Habit Learning
                13 -> {
                    var tName by remember { mutableStateOf("") }
                    var tCondition by remember { mutableStateOf("09:00 PM") }
                    var tAction by remember { mutableStateOf("Speak Daily Summary") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Habit Learning Engine", style = MaterialTheme.typography.titleMedium)
                            Text("Learns preferred commands, greetings & routines", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = config.isHabitLearningEnabled,
                            onCheckedChange = { viewModel.toggleHabitLearning(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    triggers.forEach { item ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${item.name} (${item.triggerType})", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    Text("When: ${item.condition} -> ${item.action}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Switch(
                                    checked = item.isEnabled,
                                    onCheckedChange = { viewModel.toggleAutomationTrigger(item.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tName,
                        onValueChange = { tName = it },
                        label = { Text("New Trigger Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tCondition,
                            onValueChange = { tCondition = it },
                            label = { Text("Condition") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tAction,
                            onValueChange = { tAction = it },
                            label = { Text("Action") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.addAutomationTrigger(tName, "Custom Trigger", tCondition, tAction)
                            tName = ""
                        }
                    ) {
                        Text("Add Automation Trigger")
                    }
                }

                // 14. Safety Rules
                14 -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("1. OTP & Password Privacy Guard", style = MaterialTheme.typography.titleMedium)
                                    Text("Never speaks OTP or passwords aloud unless explicitly verified.", style = MaterialTheme.typography.bodyMedium)
                                }
                                Switch(
                                    checked = config.otpPrivacyGuard,
                                    onCheckedChange = {
                                        viewModel.toggleSafetyGuards(it, config.callRejectGuard, config.honestTrafficOfflineFallback)
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("2. Call Reject Confirmation Guard", style = MaterialTheme.typography.titleMedium)
                                    Text("Requires explicit confirmation before rejecting incoming calls.", style = MaterialTheme.typography.bodyMedium)
                                }
                                Switch(
                                    checked = config.callRejectGuard,
                                    onCheckedChange = {
                                        viewModel.toggleSafetyGuards(config.otpPrivacyGuard, it, config.honestTrafficOfflineFallback)
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("3. Honest Traffic & Offline Maps Fallback", style = MaterialTheme.typography.titleMedium)
                                    Text("Never fabricates live traffic; falls back to offline GPS & Google Maps.", style = MaterialTheme.typography.bodyMedium)
                                }
                                Switch(
                                    checked = config.honestTrafficOfflineFallback,
                                    onCheckedChange = {
                                        viewModel.toggleSafetyGuards(config.otpPrivacyGuard, config.callRejectGuard, it)
                                    }
                                )
                            }

                            Button(
                                onClick = {
                                    onDismiss()
                                    viewModel.lockAppNow()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Instant Lock")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("4. Trigger Instant 'Lock' PIN Screen Now")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AssistChipButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label)
    }
}

@Composable
private fun ToolAiResultBox(
    toolAiState: ToolAiOutputState,
    viewModel: KayraViewModel
) {
    when (toolAiState) {
        is ToolAiOutputState.Idle -> {}
        is ToolAiOutputState.Loading -> {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Gemini AI is synthesizing output (may take up to 30-60s)...")
            }
        }
        is ToolAiOutputState.TextSuccess -> {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GEMINI AI OUTPUT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                        Row {
                            IconButton(onClick = { viewModel.voiceManager.copyToClipboard("Kayra Tool Output", toolAiState.text) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Output")
                            }
                            IconButton(onClick = { viewModel.speakText(toolAiState.text) }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak Output")
                            }
                        }
                    }
                    Text(toolAiState.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        is ToolAiOutputState.ImageSuccess -> {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("GENERATED IMAGE (${toolAiState.prompt})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = toolAiState.bitmap.asImageBitmap(),
                        contentDescription = toolAiState.prompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        is ToolAiOutputState.Error -> {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(toolAiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PinLockOverlay(
    aiName: String,
    expectedPin: String,
    onUnlockSuccess: (String) -> Boolean
) {
    var entered by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .testTag("pin_lock_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$aiName SECURITY SHIELD",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Enter 4-Digit PIN to Unlock (Default: $expectedPin)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(4) { idx ->
                    val filled = idx < entered.length
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                    )
                }
            }

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("CLR", "0", "OK")
            )
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    row.forEach { key ->
                        OutlinedButton(
                            onClick = {
                                when (key) {
                                    "CLR" -> {
                                        entered = ""
                                        errorMsg = ""
                                    }
                                    "OK" -> {
                                        if (!onUnlockSuccess(entered)) {
                                            errorMsg = "Incorrect PIN. Try $expectedPin"
                                            entered = ""
                                        }
                                    }
                                    else -> {
                                        if (entered.length < 4) {
                                            entered += key
                                            if (entered.length == 4) {
                                                if (!onUnlockSuccess(entered)) {
                                                    errorMsg = "Incorrect PIN. Try $expectedPin"
                                                    entered = ""
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Text(key, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenTorchOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onDismiss() }
            .testTag("fullscreen_torch_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FlashlightOn,
                contentDescription = "Max Brightness Torch",
                tint = Color.Black,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "MAX-BRIGHTNESS SCREEN TORCH ACTIVE",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap anywhere to turn off",
                color = Color.DarkGray
            )
        }
    }
}
