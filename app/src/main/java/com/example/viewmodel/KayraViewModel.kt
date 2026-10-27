package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ALL_14_TOOL_CATEGORIES
import com.example.model.ActiveMission
import com.example.model.AppNotificationItem
import com.example.model.AutomationTriggerItem
import com.example.model.ChatBubbleStyle
import com.example.model.ChatMessage
import com.example.model.ConnectorTool
import com.example.model.KayraConfig
import com.example.model.KayraTab
import com.example.model.ParkingSpot
import com.example.model.SavedContact
import com.example.model.ToolCategorySpec
import com.example.model.VaultNote
import com.example.ui.theme.NeonThemePreset
import com.example.util.DeviceDiagnostics
import com.example.util.VoiceAndAudioManager
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "KayraViewModel"

sealed interface ToolAiOutputState {
    data object Idle : ToolAiOutputState
    data object Loading : ToolAiOutputState
    data class TextSuccess(val text: String) : ToolAiOutputState
    data class ImageSuccess(val bitmap: Bitmap, val prompt: String) : ToolAiOutputState
    data class Error(val message: String) : ToolAiOutputState
}

class KayraViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kayra_companion_prefs", Context.MODE_PRIVATE)
    private var hasAutoGreeted = false

    val voiceManager = VoiceAndAudioManager(application) {
        if (!hasAutoGreeted) {
            hasAutoGreeted = true
            speakGreetingAloud()
        }
    }

    // Lazy Firebase AI Logic models per skill guidelines
    private val flashModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }
    private val proModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel("gemini-4-argon")
    }
    private val liteModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.5-flash-lite")
    }
    private val imageGenModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<KayraConfig> = _config.asStateFlow()

    private val _currentTab = MutableStateFlow(KayraTab.HOME)
    val currentTab: StateFlow<KayraTab> = _currentTab.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_1",
                isUser = false,
                text = "Namaste ${_config.value.userName}! Main ${_config.value.aiName} hoon — your futuristic AI companion powered by Gemini. Tap the glowing Mic Orb, ask me anything in Hindi, Hinglish, or English, or open any of my 14 interactive tool categories!",
                timestamp = nowTimeString()
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    private val _selectedToolCategory = MutableStateFlow<ToolCategorySpec?>(null)
    val selectedToolCategory: StateFlow<ToolCategorySpec?> = _selectedToolCategory.asStateFlow()

    private val _showCustomizeStudio = MutableStateFlow(false)
    val showCustomizeStudio: StateFlow<Boolean> = _showCustomizeStudio.asStateFlow()

    private val _isFullScreenTorchOn = MutableStateFlow(false)
    val isFullScreenTorchOn: StateFlow<Boolean> = _isFullScreenTorchOn.asStateFlow()

    private val _isHardwareTorchOn = MutableStateFlow(false)
    val isHardwareTorchOn: StateFlow<Boolean> = _isHardwareTorchOn.asStateFlow()

    // Live Header Timer state
    private val _timerSecondsRemaining = MutableStateFlow(0)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()
    private var timerJob: Job? = null

    // Synth Music state
    private val _isSynthPlaying = MutableStateFlow(false)
    val isSynthPlaying: StateFlow<Boolean> = _isSynthPlaying.asStateFlow()
    private val _currentSynthTrack = MutableStateFlow(voiceManager.synthTrackNames.first())
    val currentSynthTrack: StateFlow<String> = _currentSynthTrack.asStateFlow()

    // Live GPS coordinates
    private val _liveLat = MutableStateFlow(28.6139)
    val liveLat: StateFlow<Double> = _liveLat.asStateFlow()
    private val _liveLng = MutableStateFlow(77.2090)
    val liveLng: StateFlow<Double> = _liveLng.asStateFlow()
    private val _gpsSourceLabel = MutableStateFlow("Tap 'Refresh GPS' to lock satellite")
    val gpsSourceLabel: StateFlow<String> = _gpsSourceLabel.asStateFlow()

    // Device Diagnostics
    private val _diagnostics = MutableStateFlow(voiceManager.getDeviceDiagnostics())
    val diagnostics: StateFlow<DeviceDiagnostics> = _diagnostics.asStateFlow()

    // Tool Modal AI State (Website Builder, Image Gen, Deep Research, Game Coach, OCR)
    private val _toolAiState = MutableStateFlow<ToolAiOutputState>(ToolAiOutputState.Idle)
    val toolAiState: StateFlow<ToolAiOutputState> = _toolAiState.asStateFlow()

    // Active Autonomous Mission
    private val _activeMission = MutableStateFlow<ActiveMission?>(null)
    val activeMission: StateFlow<ActiveMission?> = _activeMission.asStateFlow()
    private var missionJob: Job? = null

    // Saved Contacts
    private val _contacts = MutableStateFlow(loadContacts())
    val contacts: StateFlow<List<SavedContact>> = _contacts.asStateFlow()

    // Private Vault Notes & Parking Spots
    private val _vaultNotes = MutableStateFlow(loadVaultNotes())
    val vaultNotes: StateFlow<List<VaultNote>> = _vaultNotes.asStateFlow()

    private val _parkingSpots = MutableStateFlow(loadParkingSpots())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    // Notifications & OTP Safety Rule #14
    private val _notifications = MutableStateFlow(
        listOf(
            AppNotificationItem(
                id = "n1",
                appName = "WhatsApp",
                sender = "Rohan Sharma",
                message = "Bhai meeting 5 PM pe start hogi, join link bhej diya hai.",
                time = "2m ago"
            ),
            AppNotificationItem(
                id = "n2",
                appName = "HDFC Bank",
                sender = "VM-HDFCBK",
                message = "Your OTP for CyberVault transaction is 849201. Do not share this OTP with anyone.",
                time = "5m ago",
                isOtp = true
            ),
            AppNotificationItem(
                id = "n3",
                appName = "Phone",
                sender = "Priya Verma (+91 98765 43210)",
                message = "Missed voice call (2 attempts)",
                time = "14m ago",
                isMissedCall = true
            ),
            AppNotificationItem(
                id = "n4",
                appName = "Google Calendar",
                sender = "Kayra Sync",
                message = "AI Product Demo & Architecture Review in 30 minutes.",
                time = "28m ago"
            )
        )
    )
    val notifications: StateFlow<List<AppNotificationItem>> = _notifications.asStateFlow()

    // Automation Triggers
    private val _automationTriggers = MutableStateFlow(
        listOf(
            AutomationTriggerItem(
                id = "t1",
                name = "Morning Briefing",
                triggerType = "Scheduled Time",
                condition = "08:00 AM Daily",
                action = "Speak Weather, Calendar & Unread Messages",
                isEnabled = true
            ),
            AutomationTriggerItem(
                id = "t2",
                name = "Low Battery Shield",
                triggerType = "Charging/Battery Trigger",
                condition = "Battery < 20%",
                action = "Announce Low Battery & Switch to Eco Mode",
                isEnabled = true
            ),
            AutomationTriggerItem(
                id = "t3",
                name = "Headphones Connected",
                triggerType = "Screen/Headphones Trigger",
                condition = "Bluetooth Audio Paired",
                action = "Read Notifications Aloud & Start Synth",
                isEnabled = true
            ),
            AutomationTriggerItem(
                id = "t4",
                name = "Home Wi-Fi Arrival",
                triggerType = "App/Wi-Fi Trigger",
                condition = "Connected to Home_5G",
                action = "Sync PC Connect & Vault Backup",
                isEnabled = false
            )
        )
    )
    val automationTriggers: StateFlow<List<AutomationTriggerItem>> = _automationTriggers.asStateFlow()

    // 8 Connector Tools
    private val _connectors = MutableStateFlow(
        listOf(
            ConnectorTool("c1", "Google Calendar & Services", "Sync events, reminders & daily agenda", "Productivity", true, "calendar.google.com"),
            ConnectorTool("c2", "Google Drive", "Cloud backup for notes, exports & OCR docs", "Cloud Storage", true, "drive.google.com"),
            ConnectorTool("c3", "GitHub", "Inspect repositories, issues & code snippets", "Developer", true, "github.com"),
            ConnectorTool("c4", "Canva Studio", "Launch visual templates & social graphics", "Design", false, "canva.com"),
            ConnectorTool("c5", "Multi-AI Router", "Gemini 3.8 Flash / Argon + External LLM Bridge", "AI Engine", true, "Firebase AI Logic Active"),
            ConnectorTool("c6", "Tavily Deep Search", "Real-time web citation & research synthesizer", "Search", true, "Research Agent Linked"),
            ConnectorTool("c7", "ElevenLabs / Neural TTS", "Bilingual hi-IN / en-IN expressive voice engine", "Voice", true, "Android Neural TTS Active"),
            ConnectorTool("c8", "Telegram Bot Bridge", "Send instant encrypted alerts & voice notes", "Messaging", false, "t.me")
        )
    )
    val connectors: StateFlow<List<ConnectorTool>> = _connectors.asStateFlow()

    // Screen Automation Macros
    private val _macros = MutableStateFlow(
        listOf(
            "Tap(Search Bar) -> Type('Latest AI News') -> Scroll(Down 2x) -> Read Screen Aloud",
            "Open(WhatsApp) -> Tap(Unread Chat) -> Speak('Replying on your behalf')",
            "Scroll(Feed) -> Extract Text(OCR) -> Save Summary to Private Vault"
        )
    )
    val macros: StateFlow<List<String>> = _macros.asStateFlow()

    private fun getActiveChatModel(): GenerativeModel {
        return when (_config.value.selectedModel) {
            "gemini-4-argon" -> proModel
            "gemini-3.5-flash-lite" -> liteModel
            else -> flashModel
        }
    }

    fun selectTab(tab: KayraTab) {
        _currentTab.value = tab
    }

    fun openToolCategory(category: ToolCategorySpec?) {
        _toolAiState.value = ToolAiOutputState.Idle
        _selectedToolCategory.value = category
        if (category != null) {
            refreshDiagnostics()
        }
    }

    fun setShowCustomizeStudio(show: Boolean) {
        _showCustomizeStudio.value = show
    }

    fun speakGreetingAloud() {
        val cfg = _config.value
        voiceManager.speak(cfg.customGreeting, cfg.isVoiceMuted, cfg.ttsVolume)
    }

    fun speakText(text: String) {
        val cfg = _config.value
        voiceManager.speak(text, cfg.isVoiceMuted, cfg.ttsVolume)
    }

    fun toggleVoiceMute() {
        val updated = _config.value.copy(isVoiceMuted = !_config.value.isVoiceMuted)
        saveAndApplyConfig(updated)
        if (updated.isVoiceMuted) {
            voiceManager.stopSpeaking()
        } else {
            speakText("${updated.aiName} voice output enabled.")
        }
    }

    fun updateTtsVolume(volume: Float) {
        val coerced = volume.coerceIn(0f, 1f)
        val updated = _config.value.copy(ttsVolume = coerced)
        saveAndApplyConfig(updated)
        voiceManager.setSystemMediaVolume(coerced)
    }

    fun updateSelectedModel(modelName: String) {
        saveAndApplyConfig(_config.value.copy(selectedModel = modelName))
    }

    fun updateCustomization(
        userName: String,
        aiName: String,
        wakeWord: String,
        customGreeting: String,
        themePreset: NeonThemePreset,
        isDarkMode: Boolean,
        bubbleStyle: ChatBubbleStyle,
        pinCode: String
    ) {
        val cleanUser = userName.trim().ifEmpty { "Amit" }
        val cleanAi = aiName.trim().ifEmpty { "KAYRA" }
        val cleanWake = wakeWord.trim().ifEmpty { "Hey $cleanAi" }
        val cleanGreeting = customGreeting.trim().ifEmpty {
            "Hello $cleanUser! Main $cleanAi hoon. How can I assist you today?"
        }
        val cleanPin = pinCode.filter { it.isDigit() }.take(4).padEnd(4, '0')
        val updated = _config.value.copy(
            userName = cleanUser,
            aiName = cleanAi,
            wakeWord = cleanWake,
            customGreeting = cleanGreeting,
            themePreset = themePreset,
            isDarkMode = isDarkMode,
            chatBubbleStyle = bubbleStyle,
            pinCode = cleanPin
        )
        saveAndApplyConfig(updated)
    }

    fun lockAppNow() {
        _config.update { it.copy(isPinLocked = true) }
        voiceManager.stopSpeaking()
    }

    fun unlockWithPin(enteredPin: String): Boolean {
        return if (enteredPin == _config.value.pinCode) {
            _config.update { it.copy(isPinLocked = false) }
            speakText("Welcome back, ${_config.value.userName}.")
            true
        } else {
            false
        }
    }

    // Countdown timer in Header
    fun startCountdownTimer(seconds: Int) {
        timerJob?.cancel()
        _timerSecondsRemaining.value = seconds.coerceAtLeast(1)
        speakText("Timer set for $seconds seconds.")
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000L)
                _timerSecondsRemaining.value -= 1
            }
            speakText("Attention ${_config.value.userName}! Your ${_config.value.aiName} countdown timer is complete!")
        }
    }

    fun cancelCountdownTimer() {
        timerJob?.cancel()
        _timerSecondsRemaining.value = 0
    }

    // Torch controls
    fun toggleHardwareTorch() {
        val target = !_isHardwareTorchOn.value
        val ok = voiceManager.toggleHardwareFlashlight(target)
        _isHardwareTorchOn.value = if (ok) target else false
        if (!ok && target) {
            _isFullScreenTorchOn.value = true
        }
    }

    fun setFullScreenTorch(enabled: Boolean) {
        _isFullScreenTorchOn.value = enabled
    }

    // Synth Music controls
    fun toggleCyberSynth() {
        if (_isSynthPlaying.value) {
            voiceManager.stopCyberSynth()
            _isSynthPlaying.value = false
        } else {
            voiceManager.startCyberSynth(viewModelScope, _config.value.ttsVolume) { playing, track ->
                _isSynthPlaying.value = playing
                _currentSynthTrack.value = track
            }
        }
    }

    fun nextCyberSynthTrack() {
        voiceManager.nextSynthTrack(viewModelScope, _config.value.ttsVolume) { playing, track ->
            _isSynthPlaying.value = playing
            _currentSynthTrack.value = track
        }
    }

    fun refreshDiagnostics() {
        _diagnostics.value = voiceManager.getDeviceDiagnostics()
    }

    fun refreshGpsLocation(onComplete: ((Double, Double) -> Unit)? = null) {
        voiceManager.fetchLiveGpsLocation { lat, lng, label ->
            _liveLat.value = lat
            _liveLng.value = lng
            _gpsSourceLabel.value = label
            onComplete?.invoke(lat, lng)
        }
    }

    // Process Voice or Chat Command (supports Hindi/Hinglish/English commands + Safety Rule #14)
    fun processCommandOrChat(rawInput: String, attachedBitmap: Bitmap? = null) {
        val text = rawInput.trim()
        if (text.isEmpty() && attachedBitmap == null) return

        val lower = text.lowercase(Locale.ROOT)

        // Safety Rule #14: Instant "Lock" Command
        if (lower == "lock" || lower == "lock device" || lower == "lock screen" || lower == "kayra lock") {
            lockAppNow()
            return
        }

        // Voice Note Vault trigger ("Mera secret note save karo..." / "save note ...")
        if (lower.contains("secret note") || lower.startsWith("save note") || lower.contains("note save karo")) {
            val content = text
                .replace(Regex("(?i)mera secret note save karo:?"), "")
                .replace(Regex("(?i)save note:?"), "")
                .trim()
                .ifEmpty { text }
            addVaultNote("Voice Vault Note", content, isVoiceNote = true)
            appendUserAndBotMessage(
                userText = text,
                botText = "सुरक्षित नोट सेव हो गया! Saved to your local Private Notes Vault: \"$content\""
            )
            return
        }

        // Parking Saver voice shortcut
        if (lower.contains("save parking") || lower.contains("parked my car") || lower.contains("parking save")) {
            saveCurrentParkingSpot("Voice Saved Vehicle Spot", text)
            appendUserAndBotMessage(
                userText = text,
                botText = "Parking location (${String.format(Locale.US, "%.4f, %.4f", _liveLat.value, _liveLng.value)}) saved to your Private Vault!"
            )
            return
        }

        // Timer voice shortcut
        if (lower.startsWith("start timer") || lower.startsWith("set timer")) {
            val digits = Regex("\\d+").find(lower)?.value?.toIntOrNull() ?: 60
            val seconds = if (lower.contains("min")) digits * 60 else digits
            startCountdownTimer(seconds)
            appendUserAndBotMessage(
                userText = text,
                botText = "Live countdown timer started for $seconds seconds in the top header."
            )
            return
        }

        // Send to Gemini AI (with streaming for text or multimodal for attached image)
        sendChatMessage(text.ifEmpty { "Analyze this image and extract any text or key details." }, attachedBitmap)
    }

    private fun appendUserAndBotMessage(userText: String, botText: String) {
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = userText,
            timestamp = nowTimeString()
        )
        val botMsg = ChatMessage(
            id = "b_${System.currentTimeMillis() + 1}",
            isUser = false,
            text = botText,
            timestamp = nowTimeString()
        )
        _messages.update { it + userMsg + botMsg }
        speakText(botText)
    }

    fun sendChatMessage(prompt: String, bitmap: Bitmap? = null) {
        val cfg = _config.value
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = prompt,
            timestamp = nowTimeString(),
            attachedBitmap = bitmap
        )
        val botMsgId = "b_${System.currentTimeMillis() + 1}"
        val placeholderBot = ChatMessage(
            id = botMsgId,
            isUser = false,
            text = "",
            timestamp = nowTimeString(),
            isStreaming = true
        )
        _messages.update { it + userMsg + placeholderBot }
        _isChatGenerating.value = true

        val systemPersona = """
            You are ${cfg.aiName}, a futuristic cyberpunk AI Companion created for ${cfg.userName}.
            You understand and reply naturally in English, Hindi, and Hinglish.
            Active Smart Mode: ${cfg.activeSmartMode}.
            Safety Rule #14: Never speak or expose raw OTPs or passwords unless the user explicitly verifies.
            Keep answers helpful, well-structured, and concise.
        """.trimIndent()

        viewModelScope.launch {
            val accumulated = StringBuilder()
            try {
                val model = getActiveChatModel()
                withTimeout(60_000L) {
                    if (bitmap != null) {
                        val response = model.generateContent(
                            content {
                                image(bitmap)
                                text("$systemPersona\n\nUser request: $prompt")
                            }
                        )
                        accumulated.append(response.text.orEmpty())
                        updateBotMessage(botMsgId, accumulated.toString(), isStreaming = false, isError = false)
                    } else {
                        model.generateContentStream("$systemPersona\n\nUser (${cfg.userName}): $prompt")
                            .collect { chunk ->
                                accumulated.append(chunk.text.orEmpty())
                                updateBotMessage(botMsgId, accumulated.toString(), isStreaming = true, isError = false)
                            }
                    }
                }
                val finalReply = accumulated.toString().trim()
                if (finalReply.isNotEmpty()) {
                    updateBotMessage(botMsgId, finalReply, isStreaming = false, isError = false)
                    speakText(finalReply)
                } else {
                    updateBotMessage(botMsgId, "Model returned an empty response.", isStreaming = false, isError = true)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat generation timed out", e)
                updateBotMessage(
                    botMsgId,
                    "Generation timed out after 60 seconds. Please try again.",
                    isStreaming = false,
                    isError = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat generation failed", e)
                updateBotMessage(
                    botMsgId,
                    e.localizedMessage ?: "AI generation failed.",
                    isStreaming = false,
                    isError = true
                )
            } finally {
                _isChatGenerating.value = false
            }
        }
    }

    private fun updateBotMessage(id: String, text: String, isStreaming: Boolean, isError: Boolean) {
        _messages.update { list ->
            list.map { msg ->
                if (msg.id == id) msg.copy(text = text, isStreaming = isStreaming, isError = isError) else msg
            }
        }
    }

    // Tool 5 & 6 AI Actions: Vision OCR, Website Builder, Deep Research, Game Coach, Runtime Image Generator
    fun runToolTextGeneration(taskTitle: String, prompt: String, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            _toolAiState.value = ToolAiOutputState.Loading
            try {
                val model = getActiveChatModel()
                val response = withTimeout(60_000L) {
                    if (bitmap != null) {
                        model.generateContent(
                            content {
                                image(bitmap)
                                text(prompt)
                            }
                        )
                    } else {
                        model.generateContent(prompt)
                    }
                }
                val out = response.text
                if (!out.isNullOrBlank()) {
                    _toolAiState.value = ToolAiOutputState.TextSuccess(out)
                    speakText("$taskTitle completed for ${_config.value.userName}.")
                } else {
                    _toolAiState.value = ToolAiOutputState.Error("Model returned an empty response.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Tool AI timed out", e)
                _toolAiState.value = ToolAiOutputState.Error("Timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Tool AI generation failed", e)
                _toolAiState.value = ToolAiOutputState.Error(e.localizedMessage ?: "Generation failed.")
            }
        }
    }

    fun generateRuntimeImage(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _toolAiState.value = ToolAiOutputState.Loading
            try {
                val bitmap = withTimeout(60_000L) {
                    val response = imageGenModel.generateContent(prompt)
                    response.candidates.firstOrNull()
                        ?.content?.parts?.filterIsInstance<ImagePart>()
                        ?.firstOrNull()?.image
                }
                if (bitmap != null) {
                    _toolAiState.value = ToolAiOutputState.ImageSuccess(bitmap, prompt)
                    speakText("Your AI image has been synthesized.")
                } else {
                    _toolAiState.value = ToolAiOutputState.Error("No image was returned by the model.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Image generation timed out", e)
                _toolAiState.value = ToolAiOutputState.Error("Image generation timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Runtime image generation failed", e)
                _toolAiState.value = ToolAiOutputState.Error(e.localizedMessage ?: "Image generation failed.")
            }
        }
    }

    // Tool 7: Autonomous Multi-Step Missions
    fun startMission(missionGoal: String) {
        if (missionGoal.isBlank()) return
        missionJob?.cancel()
        val steps = listOf(
            "Step 1/4: Parsing goal & scanning local context for '$missionGoal'",
            "Step 2/4: Querying Gemini AI planner & verifying safety guards",
            "Step 3/4: Synthesizing actionable execution steps & resources",
            "Step 4/4: Finalizing autonomous report & saving to Vault"
        )
        _activeMission.value = ActiveMission(
            title = missionGoal,
            steps = steps,
            currentStepIndex = 0,
            isPaused = false,
            isCompleted = false,
            aiLog = "Initializing KAYRA Autonomous Agent..."
        )
        speakText("Autonomous mission started: $missionGoal")

        missionJob = viewModelScope.launch {
            for (idx in steps.indices) {
                while (_activeMission.value?.isPaused == true) {
                    delay(400L)
                }
                _activeMission.update { it?.copy(currentStepIndex = idx) }
                delay(1200L)
            }
            try {
                val response = withTimeout(60_000L) {
                    flashModel.generateContent(
                        "Create a concise 4-point autonomous execution summary for user ${_config.value.userName} on mission: $missionGoal"
                    )
                }
                val summary = response.text?.trim().orEmpty().ifEmpty { "Mission steps executed." }
                _activeMission.update {
                    it?.copy(
                        currentStepIndex = steps.lastIndex,
                        isCompleted = true,
                        aiLog = summary
                    )
                }
                addVaultNote("Mission Report: $missionGoal", summary, isVoiceNote = false)
                speakText("Mission complete! Report saved to your Private Vault.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Mission AI step failed", e)
                _activeMission.update {
                    it?.copy(
                        isCompleted = true,
                        aiLog = "Mission finished with AI error: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun togglePauseMission() {
        _activeMission.update { current ->
            current?.copy(isPaused = !current.isPaused)
        }
    }

    fun cancelMission() {
        missionJob?.cancel()
        _activeMission.value = null
        speakText("Mission cancelled.")
    }

    // Tool 8 & 14: Notifications with OTP Safety Rule #14
    fun readSafeNotificationsAloud() {
        val list = _notifications.value
        if (list.isEmpty()) {
            speakText("You have no unread notifications.")
            return
        }
        val sb = StringBuilder("You have ${list.size} notifications. ")
        list.forEachIndexed { idx, item ->
            if (item.isOtp && _config.value.otpPrivacyGuard) {
                sb.append("Notification ${idx + 1} from ${item.sender}: Sensitive OTP hidden by Safety Rule 14. ")
            } else {
                sb.append("Notification ${idx + 1} from ${item.sender}: ${item.message}. ")
            }
        }
        speakText(sb.toString())
    }

    fun readOtpExplicitlyVerified() {
        val otpItems = _notifications.value.filter { it.isOtp }
        if (otpItems.isEmpty()) {
            speakText("No OTP messages found.")
        } else {
            val msg = otpItems.joinToString(". ") { "Verified OTP from ${it.sender}: ${it.message}" }
            speakText(msg)
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
        speakText("All notifications cleared.")
    }

    // Contacts management
    fun addContact(name: String, phone: String, relation: String) {
        if (name.isBlank() || phone.isBlank()) return
        val newContact = SavedContact(
            id = "c_${System.currentTimeMillis()}",
            name = name.trim(),
            phone = phone.trim(),
            relation = relation.trim().ifEmpty { "Contact" }
        )
        val updated = _contacts.value + newContact
        _contacts.value = updated
        saveContacts(updated)
    }

    fun deleteContact(id: String) {
        val updated = _contacts.value.filterNot { it.id == id }
        _contacts.value = updated
        saveContacts(updated)
    }

    // Private Vault Notes & Parking Saver
    fun addVaultNote(title: String, content: String, isVoiceNote: Boolean = false) {
        if (content.isBlank()) return
        val note = VaultNote(
            id = "vn_${System.currentTimeMillis()}",
            title = title.trim().ifEmpty { "Secret Vault Note" },
            content = content.trim(),
            timestamp = nowTimeString(),
            isVoiceNote = isVoiceNote
        )
        val updated = listOf(note) + _vaultNotes.value
        _vaultNotes.value = updated
        saveVaultNotes(updated)
    }

    fun deleteVaultNote(id: String) {
        val updated = _vaultNotes.value.filterNot { it.id == id }
        _vaultNotes.value = updated
        saveVaultNotes(updated)
    }

    fun saveCurrentParkingSpot(label: String, notes: String) {
        refreshGpsLocation { lat, lng ->
            val spot = ParkingSpot(
                id = "pk_${System.currentTimeMillis()}",
                label = label.trim().ifEmpty { "Parked Vehicle" },
                latitude = lat,
                longitude = lng,
                timestamp = nowTimeString(),
                notes = notes.trim().ifEmpty { "Saved via Kayra Parking Saver" }
            )
            val updated = listOf(spot) + _parkingSpots.value
            _parkingSpots.value = updated
            saveParkingSpots(updated)
        }
    }

    fun deleteParkingSpot(id: String) {
        val updated = _parkingSpots.value.filterNot { it.id == id }
        _parkingSpots.value = updated
        saveParkingSpots(updated)
    }

    // Smart Modes, Triggers, Connectors & Macros
    fun setSmartMode(mode: String) {
        saveAndApplyConfig(_config.value.copy(activeSmartMode = mode))
        speakText("Switched to $mode mode.")
    }

    fun toggleHabitLearning(enabled: Boolean) {
        saveAndApplyConfig(_config.value.copy(isHabitLearningEnabled = enabled))
    }

    fun toggleSafetyGuards(otpGuard: Boolean, callGuard: Boolean, trafficFallback: Boolean) {
        saveAndApplyConfig(
            _config.value.copy(
                otpPrivacyGuard = otpGuard,
                callRejectGuard = callGuard,
                honestTrafficOfflineFallback = trafficFallback
            )
        )
    }

    fun updatePcConnection(ip: String, code: String) {
        saveAndApplyConfig(_config.value.copy(pcIpAddress = ip.trim(), pcPairingCode = code.trim()))
    }

    fun toggleAutomationTrigger(id: String) {
        _automationTriggers.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        }
    }

    fun addAutomationTrigger(name: String, triggerType: String, condition: String, action: String) {
        if (name.isBlank()) return
        val item = AutomationTriggerItem(
            id = "t_${System.currentTimeMillis()}",
            name = name.trim(),
            triggerType = triggerType,
            condition = condition.trim().ifEmpty { "Always" },
            action = action.trim().ifEmpty { "Speak Alert" },
            isEnabled = true
        )
        _automationTriggers.update { it + item }
    }

    fun toggleConnector(id: String) {
        _connectors.update { list ->
            list.map { if (it.id == id) it.copy(isConnected = !it.isConnected) else it }
        }
    }

    fun addScreenMacro(macroText: String) {
        if (macroText.isBlank()) return
        _macros.update { listOf(macroText.trim()) + it }
    }

    fun buildSingleFileHtmlExport(): String {
        val cfg = _config.value
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>${cfg.aiName} AI Companion - ${cfg.userName}</title>
              <style>
                body { margin:0; font-family: system-ui, sans-serif; background:#090414; color:#F5F0FF; padding:24px; }
                .card { background:#130926; border:1px solid #D946EF55; border-radius:16px; padding:20px; margin-bottom:16px; }
                h1 { color:#D946EF; margin-top:0; }
                button { background:linear-gradient(135deg,#D946EF,#8B5CF6); color:#fff; border:none; padding:12px 20px; border-radius:99px; font-weight:bold; cursor:pointer; }
              </style>
            </head>
            <body>
              <div class="card">
                <h1>Hello, ${cfg.userName}</h1>
                <p><strong>AI Companion:</strong> ${cfg.aiName} (${cfg.selectedModel})</p>
                <p><strong>Wake Word:</strong> "${cfg.wakeWord}"</p>
                <p><strong>Greeting:</strong> "${cfg.customGreeting}"</p>
                <button onclick="speechSynthesis.speak(new SpeechSynthesisUtterance('${cfg.customGreeting.replace("'", "")}'))">🔊 Speak Greeting</button>
              </div>
              <div class="card">
                <h3>14 Active Tool Categories</h3>
                <ul>
                  ${ALL_14_TOOL_CATEGORIES.joinToString("\n") { "<li><strong>${it.title}</strong>: ${it.summary}</li>" }}
                </ul>
              </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun saveAndApplyConfig(newConfig: KayraConfig) {
        _config.value = newConfig
        prefs.edit()
            .putString("userName", newConfig.userName)
            .putString("aiName", newConfig.aiName)
            .putString("wakeWord", newConfig.wakeWord)
            .putString("customGreeting", newConfig.customGreeting)
            .putString("selectedModel", newConfig.selectedModel)
            .putString("themePreset", newConfig.themePreset.name)
            .putBoolean("isDarkMode", newConfig.isDarkMode)
            .putString("chatBubbleStyle", newConfig.chatBubbleStyle.name)
            .putBoolean("isVoiceMuted", newConfig.isVoiceMuted)
            .putFloat("ttsVolume", newConfig.ttsVolume)
            .putString("pinCode", newConfig.pinCode)
            .putBoolean("isHabitLearningEnabled", newConfig.isHabitLearningEnabled)
            .putBoolean("callRejectGuard", newConfig.callRejectGuard)
            .putBoolean("otpPrivacyGuard", newConfig.otpPrivacyGuard)
            .putBoolean("honestTrafficOfflineFallback", newConfig.honestTrafficOfflineFallback)
            .putString("activeSmartMode", newConfig.activeSmartMode)
            .putString("pcIpAddress", newConfig.pcIpAddress)
            .putString("pcPairingCode", newConfig.pcPairingCode)
            .apply()
    }

    private fun loadConfig(): KayraConfig {
        val presetName = prefs.getString("themePreset", NeonThemePreset.NEON_PURPLE_AURORA.name)
        val preset = NeonThemePreset.entries.find { it.name == presetName } ?: NeonThemePreset.NEON_PURPLE_AURORA
        val bubbleName = prefs.getString("chatBubbleStyle", ChatBubbleStyle.GLASSMORPHIC.name)
        val bubble = ChatBubbleStyle.entries.find { it.name == bubbleName } ?: ChatBubbleStyle.GLASSMORPHIC

        return KayraConfig(
            userName = prefs.getString("userName", "Amit") ?: "Amit",
            aiName = prefs.getString("aiName", "KAYRA") ?: "KAYRA",
            wakeWord = prefs.getString("wakeWord", "Hey Kayra") ?: "Hey Kayra",
            customGreeting = prefs.getString(
                "customGreeting",
                "Hello Amit! Main Kayra hoon. How can I assist you today?"
            ) ?: "Hello Amit! Main Kayra hoon. How can I assist you today?",
            selectedModel = prefs.getString("selectedModel", "gemini-3.8-flash") ?: "gemini-3.8-flash",
            themePreset = preset,
            isDarkMode = prefs.getBoolean("isDarkMode", true),
            chatBubbleStyle = bubble,
            isVoiceMuted = prefs.getBoolean("isVoiceMuted", false),
            ttsVolume = prefs.getFloat("ttsVolume", 0.9f),
            pinCode = prefs.getString("pinCode", "1234") ?: "1234",
            isHabitLearningEnabled = prefs.getBoolean("isHabitLearningEnabled", true),
            callRejectGuard = prefs.getBoolean("callRejectGuard", true),
            otpPrivacyGuard = prefs.getBoolean("otpPrivacyGuard", true),
            honestTrafficOfflineFallback = prefs.getBoolean("honestTrafficOfflineFallback", true),
            activeSmartMode = prefs.getString("activeSmartMode", "Standard Cyber") ?: "Standard Cyber",
            pcIpAddress = prefs.getString("pcIpAddress", "192.168.1.42:8080") ?: "192.168.1.42:8080",
            pcPairingCode = prefs.getString("pcPairingCode", "KYR-9042") ?: "KYR-9042"
        )
    }

    private fun saveContacts(list: List<SavedContact>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("phone", it.phone)
                    put("relation", it.relation)
                }
            )
        }
        prefs.edit().putString("contacts_json", arr.toString()).apply()
    }

    private fun loadContacts(): List<SavedContact> {
        val raw = prefs.getString("contacts_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                SavedContact("c1", "Mom (Home)", "+919876543210", "Family SOS"),
                SavedContact("c2", "Rohan Sharma", "+919811122233", "Work Team"),
                SavedContact("c3", "Priya Verma", "+919822233344", "Friend")
            )
        }
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                SavedContact(
                    id = o.optString("id", "c_$i"),
                    name = o.optString("name", "Contact"),
                    phone = o.optString("phone", ""),
                    relation = o.optString("relation", "Saved")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveVaultNotes(list: List<VaultNote>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject().apply {
                    put("id", it.id)
                    put("title", it.title)
                    put("content", it.content)
                    put("timestamp", it.timestamp)
                    put("isVoiceNote", it.isVoiceNote)
                }
            )
        }
        prefs.edit().putString("vault_notes_json", arr.toString()).apply()
    }

    private fun loadVaultNotes(): List<VaultNote> {
        val raw = prefs.getString("vault_notes_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                VaultNote(
                    id = "vn_default_1",
                    title = "Private Vault Welcome Note",
                    content = "Say 'Mera secret note save karo...' or add notes here. Protected by your 4-digit PIN lock.",
                    timestamp = nowTimeString(),
                    isVoiceNote = true
                )
            )
        }
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                VaultNote(
                    id = o.optString("id"),
                    title = o.optString("title"),
                    content = o.optString("content"),
                    timestamp = o.optString("timestamp"),
                    isVoiceNote = o.optBoolean("isVoiceNote", false)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveParkingSpots(list: List<ParkingSpot>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject().apply {
                    put("id", it.id)
                    put("label", it.label)
                    put("lat", it.latitude)
                    put("lng", it.longitude)
                    put("timestamp", it.timestamp)
                    put("notes", it.notes)
                }
            )
        }
        prefs.edit().putString("parking_spots_json", arr.toString()).apply()
    }

    private fun loadParkingSpots(): List<ParkingSpot> {
        val raw = prefs.getString("parking_spots_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                ParkingSpot(
                    id = "pk_default_1",
                    label = "Cyber Hub Basement B2 - Pillar 14",
                    latitude = 28.4950,
                    longitude = 77.0895,
                    timestamp = "Today, 10:15 AM",
                    notes = "Near Elevator Lobby C"
                )
            )
        }
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                ParkingSpot(
                    id = o.optString("id"),
                    label = o.optString("label"),
                    latitude = o.optDouble("lat", 28.6139),
                    longitude = o.optDouble("lng", 77.2090),
                    timestamp = o.optString("timestamp"),
                    notes = o.optString("notes")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun nowTimeString(): String {
        return SimpleDateFormat("hh:mm a", Locale.US).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
