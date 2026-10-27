package com.example.model

import android.graphics.Bitmap
import com.example.ui.theme.NeonThemePreset

enum class ChatBubbleStyle(val displayName: String) {
    ROUNDED("Rounded"),
    GLASSMORPHIC("Glassmorphic"),
    MINIMAL("Minimal")
}

enum class KayraTab(val label: String) {
    HOME("Home"),
    CHAT("Chat"),
    MIC("Mic Orb"),
    TOOLS("Tools (14)"),
    SETTINGS("Settings")
}

data class KayraConfig(
    val userName: String = "Amit",
    val aiName: String = "KAYRA",
    val wakeWord: String = "Hey Kayra",
    val customGreeting: String = "Hello Amit! Main Kayra hoon. How can I assist you today?",
    val selectedModel: String = "gemini-3.8-flash",
    val themePreset: NeonThemePreset = NeonThemePreset.NEON_PURPLE_AURORA,
    val isDarkMode: Boolean = true,
    val chatBubbleStyle: ChatBubbleStyle = ChatBubbleStyle.GLASSMORPHIC,
    val isVoiceMuted: Boolean = false,
    val ttsVolume: Float = 0.9f,
    val pinCode: String = "1234",
    val isPinLocked: Boolean = false,
    val isHabitLearningEnabled: Boolean = true,
    val callRejectGuard: Boolean = true,
    val otpPrivacyGuard: Boolean = true,
    val honestTrafficOfflineFallback: Boolean = true,
    val activeSmartMode: String = "Standard Cyber",
    val speechLanguage: String = "en-IN",
    val pcIpAddress: String = "192.168.1.42:8080",
    val pcPairingCode: String = "KYR-9042"
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val attachedBitmap: Bitmap? = null,
    val isStreaming: Boolean = false,
    val isError: Boolean = false
)

data class SavedContact(
    val id: String,
    val name: String,
    val phone: String,
    val relation: String
)

data class VaultNote(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val isVoiceNote: Boolean = false
)

data class ParkingSpot(
    val id: String,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val notes: String
)

data class AppNotificationItem(
    val id: String,
    val appName: String,
    val sender: String,
    val message: String,
    val time: String,
    val isOtp: Boolean = false,
    val isMissedCall: Boolean = false,
    val isRead: Boolean = false
)

data class ActiveMission(
    val title: String,
    val steps: List<String>,
    val currentStepIndex: Int,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val aiLog: String = ""
)

data class AutomationTriggerItem(
    val id: String,
    val name: String,
    val triggerType: String,
    val condition: String,
    val action: String,
    val isEnabled: Boolean
)

data class ConnectorTool(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val isConnected: Boolean,
    val endpointHint: String
)

data class ToolCategorySpec(
    val id: Int,
    val title: String,
    val badge: String,
    val summary: String,
    val subTools: List<String>
)

val ALL_14_TOOL_CATEGORIES = listOf(
    ToolCategorySpec(
        id = 1,
        title = "1. Communication Tools",
        badge = "COMMS & SOS",
        summary = "WhatsApp, SMS, Email & Live GPS Emergency SOS Alert",
        subTools = listOf("WhatsApp Direct", "Send SMS", "Compose Email", "Emergency SOS (Live GPS)")
    ),
    ToolCategorySpec(
        id = 2,
        title = "2. Call Tools",
        badge = "TELEPHONY",
        summary = "Call Contact Dialer, Answer Call Config, Call Reject Guard & Contact Vault",
        subTools = listOf("Call Contact Dialer", "Answer Call Config", "End/Reject Call Guard", "Lookup / Add Contact")
    ),
    ToolCategorySpec(
        id = 3,
        title = "3. Media Tools",
        badge = "SYNTH & AUDIO",
        summary = "Built-in Cyber Synth Tune + YouTube Music Launcher, Media Controls & Volume",
        subTools = listOf("Play Cyber Synth Music", "Launch YouTube Music", "Media Control (Play/Pause/Next)", "Set TTS & Media Volume")
    ),
    ToolCategorySpec(
        id = 4,
        title = "4. Device Tools",
        badge = "HARDWARE",
        summary = "Live Header Countdown Timer, Full-Screen Torch, Battery, PIN Lock, Clipboard & Storage",
        subTools = listOf("Alarm & Live Countdown Timer", "Flashlight + Screen Torch", "Live Battery Info", "Instant 4-Digit PIN Lock", "Clipboard Manager", "Storage Analyzer")
    ),
    ToolCategorySpec(
        id = 5,
        title = "5. Files & Photos",
        badge = "VISION & OCR",
        summary = "Vault File Manager, Zip/Share Export, Camera Capture & Gemini Vision Text OCR",
        subTools = listOf("File & Vault Manager", "Zip / Share Export", "Delete Session Photos", "Live Camera Capture", "Extract Text (Gemini Vision OCR)")
    ),
    ToolCategorySpec(
        id = 6,
        title = "6. Smart / Generative Tools",
        badge = "GEMINI AI",
        summary = "Website Builder, Runtime AI Image Generator, Deep Research, Game Coach & Phone Health",
        subTools = listOf("AI Website Builder", "AI Image Generator (Nano Banana)", "Deep Research Agent", "AI Game Coach", "Live Phone Health Diagnostic")
    ),
    ToolCategorySpec(
        id = 7,
        title = "7. Missions",
        badge = "AUTONOMOUS",
        summary = "Multi-step Autonomous Mission Bar with Live Step-by-Step Progress, Pause & Cancel",
        subTools = listOf("Start Autonomous Mission", "Pause / Resume Mission", "Cancel Active Mission")
    ),
    ToolCategorySpec(
        id = 8,
        title = "8. Notifications",
        badge = "VOICE ALERTS",
        summary = "Speak Unread Notifications, Quick Reply, Missed Calls & Safety-Protected OTP Reader",
        subTools = listOf("Read Unread Notifications Aloud", "Reply to Notification", "Read Missed Calls", "Read OTP On Demand (Rule #14)", "Clear Notifications")
    ),
    ToolCategorySpec(
        id = 9,
        title = "9. Maps & 'My World'",
        badge = "GEO & PARKING",
        summary = "Google Maps Navigation, Live GPS Location, Parking Saver, Nearby Places & Smart Modes",
        subTools = listOf("Google Maps Navigation", "My Live GPS Location", "Parking Saver (GPS Vault)", "Nearby ATM / Hospital / Cafe / Fuel", "Smart Modes (Driving/Sleep/Work/Game)")
    ),
    ToolCategorySpec(
        id = 10,
        title = "10. PC Connect",
        badge = "REMOTE LINK",
        summary = "Pair PC via IP/Code, Remote Lock/Media Commands & Send File to PC",
        subTools = listOf("Connect PC (IP & Pairing Code)", "PC Remote Lock / Media Control", "Send File / Note to PC")
    ),
    ToolCategorySpec(
        id = 11,
        title = "11. Search, Browser & Apps",
        badge = "LAUNCHPAD",
        summary = "Google Search Launcher, Browser Control & 1-Tap App Launcher",
        subTools = listOf("Google Search", "Browser URL Launcher", "Open WhatsApp / YouTube / Instagram / Maps / Phone")
    ),
    ToolCategorySpec(
        id = 12,
        title = "12. Screen Automation",
        badge = "MACROS",
        summary = "Auto Screen Read Aloud, Tap/Type/Scroll Macro Builder & Background Task Queue",
        subTools = listOf("Auto Screen Read Aloud", "Tap / Type / Scroll Macro Builder", "Run Background Task Queue")
    ),
    ToolCategorySpec(
        id = 13,
        title = "13. Automation & Habit Learning",
        badge = "TRIGGERS",
        summary = "Time, Notification, Battery, Headphones & Wi-Fi Triggers + Habit Learning Engine",
        subTools = listOf("Scheduled Time Trigger", "Battery / Charging Trigger", "Headphones / Wi-Fi Trigger", "Habit Learning Toggle")
    ),
    ToolCategorySpec(
        id = 14,
        title = "14. Safety Rules & Guards",
        badge = "ZERO-TRUST",
        summary = "OTP & Password Privacy Guard, Call Reject Guard, Instant 'Lock' & Honest Traffic Fallback",
        subTools = listOf("OTP & Password Privacy Guard", "Call Reject Confirmation Guard", "Instant 'Lock' Voice/Text Trigger", "Traffic Data & Offline Maps Fallback")
    )
)
