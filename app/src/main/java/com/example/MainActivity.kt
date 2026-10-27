package com.example

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ALL_14_TOOL_CATEGORIES
import com.example.model.KayraTab
import com.example.ui.screens.ChatTabContent
import com.example.ui.screens.CustomizeUiStudioModal
import com.example.ui.screens.FullScreenTorchOverlay
import com.example.ui.screens.HomeTabContent
import com.example.ui.screens.KayraBottomBar
import com.example.ui.screens.KayraTopHeader
import com.example.ui.screens.MicOrbVoiceStudioTab
import com.example.ui.screens.PinLockOverlay
import com.example.ui.screens.SettingsTabContent
import com.example.ui.screens.ToolCategoryModal
import com.example.ui.screens.ToolsCommandCenterTab
import com.example.ui.theme.KayraTheme
import com.example.viewmodel.KayraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            KayraAppRoot()
        }
    }
}

@Composable
fun KayraAppRoot(viewModel: KayraViewModel = viewModel()) {
    val config by viewModel.config.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isChatGenerating by viewModel.isChatGenerating.collectAsState()
    val timerSeconds by viewModel.timerSecondsRemaining.collectAsState()
    val selectedToolCategory by viewModel.selectedToolCategory.collectAsState()
    val showCustomizeStudio by viewModel.showCustomizeStudio.collectAsState()
    val isFullScreenTorchOn by viewModel.isFullScreenTorchOn.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val activeMission by viewModel.activeMission.collectAsState()
    val connectors by viewModel.connectors.collectAsState()
    val vaultNotes by viewModel.vaultNotes.collectAsState()
    val parkingSpots by viewModel.parkingSpots.collectAsState()

    var lastVoiceTranscript by remember { mutableStateOf("") }

    // Speech Recognizer Activity Result Launcher (supports Hindi / Hinglish / English)
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val spokenText = matches?.firstOrNull().orEmpty()
        if (spokenText.isNotBlank()) {
            lastVoiceTranscript = spokenText
            viewModel.processCommandOrChat(spokenText)
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        try {
            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, config.speechLanguage)
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Speak to ${config.aiName} in Hindi, Hinglish, or English..."
                )
            }
            speechRecognizerLauncher.launch(speechIntent)
        } catch (e: Exception) {
            viewModel.selectTab(KayraTab.MIC)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.refreshGpsLocation()
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val triggerVoiceInput = {
        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    // BackHandler for non-Home tabs
    BackHandler(enabled = currentTab != KayraTab.HOME) {
        viewModel.selectTab(KayraTab.HOME)
    }

    KayraTheme(
        preset = config.themePreset,
        darkTheme = config.isDarkMode
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    KayraTopHeader(
                        config = config,
                        timerSeconds = timerSeconds,
                        onSelectModel = { viewModel.updateSelectedModel(it) },
                        onToggleMute = { viewModel.toggleVoiceMute() },
                        onTriggerVoiceInput = triggerVoiceInput,
                        onLockApp = { viewModel.lockAppNow() },
                        onOpenCustomizeStudio = { viewModel.setShowCustomizeStudio(true) },
                        onCancelTimer = { viewModel.cancelCountdownTimer() }
                    )
                },
                bottomBar = {
                    KayraBottomBar(
                        currentTab = currentTab,
                        onSelectTab = { viewModel.selectTab(it) },
                        onCenterMicClick = triggerVoiceInput
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        KayraTab.HOME -> {
                            HomeTabContent(
                                config = config,
                                unreadNotificationCount = notifications.size,
                                activeMission = activeMission,
                                onReplayGreeting = { viewModel.speakGreetingAloud() },
                                onStartVoiceOrb = triggerVoiceInput,
                                onOpenNotificationsTool = {
                                    val notifTool = ALL_14_TOOL_CATEGORIES.find { it.id == 8 }
                                    viewModel.openToolCategory(notifTool)
                                },
                                onQuickPrompt = { prompt ->
                                    viewModel.processCommandOrChat(prompt)
                                    if (prompt.lowercase() != "lock") {
                                        viewModel.selectTab(KayraTab.CHAT)
                                    }
                                },
                                onOpenToolCategoryId = { id ->
                                    val cat = ALL_14_TOOL_CATEGORIES.find { it.id == id }
                                    viewModel.openToolCategory(cat)
                                },
                                onPauseResumeMission = { viewModel.togglePauseMission() },
                                onCancelMission = { viewModel.cancelMission() }
                            )
                        }

                        KayraTab.CHAT -> {
                            ChatTabContent(
                                viewModel = viewModel,
                                config = config,
                                messages = messages,
                                isGenerating = isChatGenerating,
                                onStartVoiceInput = triggerVoiceInput
                            )
                        }

                        KayraTab.MIC -> {
                            MicOrbVoiceStudioTab(
                                config = config,
                                lastVoiceTranscript = lastVoiceTranscript,
                                onTriggerSystemSpeechRecognizer = triggerVoiceInput,
                                onExecuteSpokenText = { spoken ->
                                    lastVoiceTranscript = spoken
                                    viewModel.processCommandOrChat(spoken)
                                    if (!spoken.lowercase().contains("lock")) {
                                        viewModel.selectTab(KayraTab.CHAT)
                                    }
                                },
                                onSaveVoiceVaultNote = { noteText ->
                                    viewModel.addVaultNote("Spoken Secret Note", noteText, isVoiceNote = true)
                                    viewModel.speakText("Secret note saved to your private vault.")
                                }
                            )
                        }

                        KayraTab.TOOLS -> {
                            ToolsCommandCenterTab(
                                viewModel = viewModel,
                                connectors = connectors,
                                vaultNotes = vaultNotes,
                                parkingSpots = parkingSpots,
                                onSelectCategory = { viewModel.openToolCategory(it) }
                            )
                        }

                        KayraTab.SETTINGS -> {
                            SettingsTabContent(
                                viewModel = viewModel,
                                config = config,
                                onOpenCustomizeStudio = { viewModel.setShowCustomizeStudio(true) },
                                onOpenToolCategory = { viewModel.openToolCategory(it) }
                            )
                        }
                    }
                }
            }

            // Customize My UI Studio Modal
            if (showCustomizeStudio) {
                CustomizeUiStudioModal(
                    config = config,
                    onDismiss = { viewModel.setShowCustomizeStudio(false) },
                    onSave = { userName, aiName, wakeWord, greeting, preset, isDark, bubbleStyle, pinCode ->
                        viewModel.updateCustomization(
                            userName = userName,
                            aiName = aiName,
                            wakeWord = wakeWord,
                            customGreeting = greeting,
                            themePreset = preset,
                            isDarkMode = isDark,
                            bubbleStyle = bubbleStyle,
                            pinCode = pinCode
                        )
                        viewModel.speakGreetingAloud()
                    }
                )
            }

            // Interactive Tool Execution Modal (for all 14 Categories)
            if (selectedToolCategory != null) {
                ToolCategoryModal(
                    category = selectedToolCategory!!,
                    viewModel = viewModel,
                    onDismiss = { viewModel.openToolCategory(null) },
                    onRequestLocationPermission = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onRequestCameraPermission = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }

            // Full-Screen Torch Overlay
            if (isFullScreenTorchOn) {
                FullScreenTorchOverlay(
                    onDismiss = { viewModel.setFullScreenTorch(false) }
                )
            }

            // 4-Digit PIN Lock Overlay
            if (config.isPinLocked) {
                PinLockOverlay(
                    aiName = config.aiName,
                    expectedPin = config.pinCode,
                    onUnlockSuccess = { entered -> viewModel.unlockWithPin(entered) }
                )
            }
        }
    }
}
