package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.ALL_14_TOOL_CATEGORIES
import com.example.model.ConnectorTool
import com.example.model.KayraConfig
import com.example.model.ParkingSpot
import com.example.model.ToolCategorySpec
import com.example.model.VaultNote
import com.example.viewmodel.KayraViewModel
import java.util.Locale

@Composable
fun MicOrbVoiceStudioTab(
    config: KayraConfig,
    lastVoiceTranscript: String,
    onTriggerSystemSpeechRecognizer: () -> Unit,
    onExecuteSpokenText: (String) -> Unit,
    onSaveVoiceVaultNote: (String) -> Unit
) {
    var manualVoiceCommand by remember { mutableStateOf("") }
    var secretNoteDraft by remember { mutableStateOf("") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "${config.aiName} HANDS-FREE VOICE ORB",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Supports Hindi, Hinglish & English Voice Commands",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(164.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(3.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                    .clickable { onTriggerSystemSpeechRecognizer() }
                    .testTag("voice_studio_mic_orb")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Start Hands-Free Voice Recognition",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TAP TO SPEAK",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (lastVoiceTranscript.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "LAST CAPTURED VOICE COMMAND",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"$lastVoiceTranscript\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TEST OR SIMULATE VOICE COMMAND (HINDI / HINGLISH / ENGLISH)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = manualVoiceCommand,
                            onValueChange = { manualVoiceCommand = it },
                            placeholder = { Text("e.g., Mera secret note save karo: ATM PIN 9080") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (manualVoiceCommand.isNotBlank()) {
                                    onExecuteSpokenText(manualVoiceCommand)
                                    manualVoiceCommand = ""
                                }
                            }
                        ) {
                            Text("Run")
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "QUICK PRIVATE VOICE NOTE VAULT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Say \"Mera secret note save karo...\" or type below to store safely in your local Vault:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = secretNoteDraft,
                            onValueChange = { secretNoteDraft = it },
                            placeholder = { Text("Secret note content...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (secretNoteDraft.isNotBlank()) {
                                    onSaveVoiceVaultNote(secretNoteDraft)
                                    secretNoteDraft = ""
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolsCommandCenterTab(
    viewModel: KayraViewModel,
    connectors: List<ConnectorTool>,
    vaultNotes: List<VaultNote>,
    parkingSpots: List<ParkingSpot>,
    onSelectCategory: (ToolCategorySpec) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ALL_14_TOOL_CATEGORIES
        } else {
            val q = searchQuery.lowercase(Locale.ROOT)
            ALL_14_TOOL_CATEGORIES.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                    it.summary.lowercase(Locale.ROOT).contains(q) ||
                    it.subTools.any { sub -> sub.lowercase(Locale.ROOT).contains(q) }
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "KAYRA 14-TOOL COMMAND CENTER",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Tools") },
                placeholder = { Text("Search all 14 tool categories, SOS, OCR, Synth, Maps...") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tools_search_input")
            )
        }

        items(filteredCategories, key = { it.id }) { category ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCategory(category) }
                    .testTag("tool_category_card_${category.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = category.badge,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        category.subTools.take(3).forEach { sub ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = sub,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Connector Tools Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CONNECTOR TOOLS (8 INTEGRATIONS)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Google Calendar, Drive, GitHub, Canva, Multi-AI, Tavily, ElevenLabs & Telegram",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(connectors, key = { it.id }) { conn ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = BorderStroke(
                    1.dp,
                    if (conn.isConnected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = conn.name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = conn.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${conn.subtitle} • ${conn.endpointHint}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (conn.endpointHint.contains(".com") || conn.endpointHint.contains("t.me")) {
                            OutlinedButton(
                                onClick = { viewModel.voiceManager.openUrl(conn.endpointHint) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Open")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Switch(
                            checked = conn.isConnected,
                            onCheckedChange = { viewModel.toggleConnector(conn.id) }
                        )
                    }
                }
            }
        }

        // Local Private Notes Vault & Parking Saver
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIVATE NOTES & PARKING VAULT",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { viewModel.lockAppNow() }) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Vault",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Text(
                        text = "Stored locally on device. Protected by your 4-digit PIN Lock.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newNoteTitle,
                        onValueChange = { newNoteTitle = it },
                        label = { Text("Vault Note Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        label = { Text("Secret Note Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (newNoteContent.isNotBlank()) {
                                    viewModel.addVaultNote(newNoteTitle, newNoteContent)
                                    newNoteTitle = ""
                                    newNoteContent = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Save Note", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save to Vault")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.saveCurrentParkingSpot("Pinned Vehicle GPS Spot", "Saved from Vault")
                            }
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = "Save Parking", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save GPS Parking")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    vaultNotes.forEach { note ->
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
                                    Text(
                                        text = "${note.title} • ${note.timestamp}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row {
                                    IconButton(onClick = { viewModel.speakText(note.content) }) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak Note")
                                    }
                                    IconButton(onClick = { viewModel.deleteVaultNote(note.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                                    }
                                }
                            }
                        }
                    }

                    if (parkingSpots.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "SAVED PARKING SPOTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        parkingSpots.forEach { spot ->
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
                                        Text(
                                            text = "${spot.label} (${spot.timestamp})",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = String.format(Locale.US, "GPS: %.4f, %.4f • %s", spot.latitude, spot.longitude, spot.notes),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                viewModel.voiceManager.launchMapsQuery("${spot.latitude},${spot.longitude}")
                                            }
                                        ) {
                                            Icon(Icons.Default.Navigation, contentDescription = "Navigate to Car")
                                        }
                                        IconButton(onClick = { viewModel.deleteParkingSpot(spot.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Spot")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTabContent(
    viewModel: KayraViewModel,
    config: KayraConfig,
    onOpenCustomizeStudio: () -> Unit,
    onOpenToolCategory: (ToolCategorySpec) -> Unit
) {
    val context = LocalContext.current
    var userName by remember(config.userName) { mutableStateOf(config.userName) }
    var aiName by remember(config.aiName) { mutableStateOf(config.aiName) }
    var wakeWord by remember(config.wakeWord) { mutableStateOf(config.wakeWord) }
    var greeting by remember(config.customGreeting) { mutableStateOf(config.customGreeting) }
    var pinCode by remember(config.pinCode) { mutableStateOf(config.pinCode) }
    var exportCopiedBanner by remember { mutableStateOf(false) }

    val saveHtmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(viewModel.buildSingleFileHtmlExport().toByteArray(Charsets.UTF_8))
                }
                exportCopiedBanner = true
            } catch (e: Exception) {
                // Ignore write error
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "SETTINGS & CUSTOMIZATION",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onOpenCustomizeStudio,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_open_customize_studio_button")
            ) {
                Icon(Icons.Default.Palette, contentDescription = "Customize UI Studio")
                Spacer(modifier = Modifier.width(8.dp))
                Text("🎨 Open 'Customize My UI' Studio Modal")
            }
        }

        // Quick Name, Wake Word, PIN & Greeting Editor
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK IDENTITY & VOICE GREETING EDITOR",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("User Name (Default: Amit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = aiName,
                        onValueChange = { aiName = it },
                        label = { Text("AI Companion Name (Default: KAYRA)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
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
                        label = { Text("Auto-Voice Greeting Line") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.updateCustomization(
                                    userName = userName,
                                    aiName = aiName,
                                    wakeWord = wakeWord,
                                    customGreeting = greeting,
                                    themePreset = config.themePreset,
                                    isDarkMode = config.isDarkMode,
                                    bubbleStyle = config.chatBubbleStyle,
                                    pinCode = pinCode
                                )
                                viewModel.speakGreetingAloud()
                            }
                        ) {
                            Text("Save & Speak Greeting")
                        }
                    }
                }
            }
        }

        // 1-Click Download index.html / Copy Code for APK
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WEBVIEW / SINGLE-FILE HTML EXPORT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Export or copy the single-file index.html bundle with your custom ${config.aiName} profile.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { saveHtmlLauncher.launch("index.html") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_html_file_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download index.html", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⬇️ Download index.html to Device")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val html = viewModel.buildSingleFileHtmlExport()
                                viewModel.voiceManager.copyToClipboard("index.html", html)
                                exportCopiedBanner = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_html_apk_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy HTML", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Code")
                        }
                        OutlinedButton(
                            onClick = {
                                val html = viewModel.buildSingleFileHtmlExport()
                                viewModel.voiceManager.shareText("${config.aiName}_index.html", html)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share HTML", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share / Export")
                        }
                    }
                    if (exportCopiedBanner) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ Single-file index.html copied to clipboard!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // 14 App Settings Modules
        item {
            Text(
                text = "14 APP SETTINGS MODULES",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(ALL_14_TOOL_CATEGORIES, key = { it.id }) { cat ->
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenToolCategory(cat) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Module ${cat.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = cat.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = { onOpenToolCategory(cat) }) {
                        Text("Configure")
                    }
                }
            }
        }
    }
}
