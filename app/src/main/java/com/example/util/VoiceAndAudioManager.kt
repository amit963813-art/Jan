package com.example.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

data class DeviceDiagnostics(
    val batteryPct: Int,
    val isCharging: Boolean,
    val cpuCores: Int,
    val ramUsedGb: String,
    val ramTotalGb: String,
    val storageFreeGb: String,
    val storageTotalGb: String,
    val networkStatus: String
)

class VoiceAndAudioManager(
    private val context: Context,
    private val onReadyToGreet: () -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var synthJob: Job? = null
    var isSynthPlaying: Boolean = false
        private set
    var currentSynthTrackIndex: Int = 0
        private set

    val synthTrackNames = listOf(
        "Neon Horizon (Cyberpunk Arpeggio)",
        "Mumbai 2099 (Synthwave Pulse)",
        "Neural Core (Dark Ambient Bass)"
    )

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "TTS init failed", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("en", "IN")
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            tts?.setPitch(1.04f)
            tts?.setSpeechRate(1.0f)
            isTtsReady = true
            onReadyToGreet()
        }
    }

    fun speak(text: String, isMuted: Boolean, volume: Float = 0.9f) {
        if (isMuted || !isTtsReady || text.isBlank()) return
        val cleanText = text
            .replace(Regex("```[\\s\\S]*?```"), " code block ")
            .replace("*", "")
            .replace("#", "")
            .take(450)
        val params = android.os.Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.coerceIn(0f, 1f))
        }
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "kayra_utt_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun startCyberSynth(scope: CoroutineScope, volume: Float = 0.8f, onStateChanged: (Boolean, String) -> Unit) {
        stopCyberSynth()
        isSynthPlaying = true
        val trackTitle = synthTrackNames[currentSynthTrackIndex % synthTrackNames.size]
        onStateChanged(true, trackTitle)

        synthJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 22050
            val melodies = listOf(
                doubleArrayOf(220.0, 261.63, 329.63, 440.0, 329.63, 261.63, 293.66, 392.0),
                doubleArrayOf(196.0, 246.94, 293.66, 392.0, 440.0, 392.0, 293.66, 246.94),
                doubleArrayOf(130.81, 164.81, 196.0, 261.63, 196.0, 164.81, 146.83, 220.0)
            )
            val notes = melodies[currentSynthTrackIndex % melodies.size]
            var step = 0
            while (isActive && isSynthPlaying) {
                val freq = notes[step % notes.size]
                playToneBurst(sampleRate, freq, 220, volume)
                step++
                delay(40L)
            }
        }
    }

    fun nextSynthTrack(scope: CoroutineScope, volume: Float, onStateChanged: (Boolean, String) -> Unit) {
        currentSynthTrackIndex = (currentSynthTrackIndex + 1) % synthTrackNames.size
        if (isSynthPlaying) {
            startCyberSynth(scope, volume, onStateChanged)
        } else {
            onStateChanged(false, synthTrackNames[currentSynthTrackIndex])
        }
    }

    fun stopCyberSynth() {
        isSynthPlaying = false
        synthJob?.cancel()
        synthJob = null
    }

    private fun playToneBurst(sampleRate: Int, freqHz: Double, durationMs: Int, volume: Float) {
        try {
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val envelope = if (i < numSamples / 8) {
                    i.toDouble() / (numSamples / 8.0)
                } else {
                    1.0 - (i.toDouble() / numSamples.toDouble()) * 0.7
                }
                val wave = sin(2.0 * Math.PI * i / (sampleRate / freqHz)) +
                    0.35 * sin(4.0 * Math.PI * i / (sampleRate / freqHz))
                buffer[i] = (wave * envelope * 12000 * volume.coerceIn(0f, 1f)).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong())
            track.stop()
            track.release()
        } catch (e: Exception) {
            Log.w("VoiceAndAudioManager", "AudioTrack playback error", e)
        }
    }

    fun setSystemMediaVolume(percent: Float) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val target = (max * percent.coerceIn(0f, 1f)).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        } catch (e: Exception) {
            Log.w("VoiceAndAudioManager", "Volume adjustment failed", e)
        }
    }

    fun toggleHardwareFlashlight(enable: Boolean): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            cameraManager.setTorchMode(cameraId, enable)
            true
        } catch (e: Exception) {
            Log.w("VoiceAndAudioManager", "Hardware torch unavailable, using screen torch", e)
            false
        }
    }

    fun getDeviceDiagnostics(): DeviceDiagnostics {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val batteryPct = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.coerceIn(0, 100) ?: 85
        val isCharging = bm?.isCharging ?: false

        val cores = Runtime.getRuntime().availableProcessors()

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val totalRamGb = memInfo.totalMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val availRamGb = memInfo.availMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val usedRamGb = (totalRamGb - availRamGb).coerceAtLeast(0.1)

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageGb = statFs.totalBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val freeStorageGb = statFs.availableBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork)
        val netStatus = when {
            capabilities == null -> "Offline (Local Fallback Active)"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Connected (High Speed)"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "5G / Cellular Data Active"
            else -> "Online Network Active"
        }

        return DeviceDiagnostics(
            batteryPct = batteryPct,
            isCharging = isCharging,
            cpuCores = cores,
            ramUsedGb = String.format(Locale.US, "%.1f GB", usedRamGb),
            ramTotalGb = String.format(Locale.US, "%.1f GB", totalRamGb),
            storageFreeGb = String.format(Locale.US, "%.1f GB", freeStorageGb),
            storageTotalGb = String.format(Locale.US, "%.1f GB", totalStorageGb),
            networkStatus = netStatus
        )
    }

    @SuppressLint("MissingPermission")
    fun fetchLiveGpsLocation(onResult: (Double, Double, String) -> Unit) {
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        onResult(loc.latitude, loc.longitude, "Live GPS Satellite Lock")
                    } else {
                        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                        val last = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (last != null) {
                            onResult(last.latitude, last.longitude, "Last Known Device GPS")
                        } else {
                            onResult(28.6139, 77.2090, "GPS Signal Pending (Defaulting to New Delhi Hub)")
                        }
                    }
                }
                .addOnFailureListener {
                    onResult(28.6139, 77.2090, "Location Permission / Sensor Fallback")
                }
        } catch (e: Exception) {
            onResult(28.6139, 77.2090, "Location Sensor Fallback")
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        cm?.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun readFromClipboard(): String {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        return cm?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
    }

    fun launchWhatsApp(phone: String, message: String) {
        val cleanPhone = phone.filter { it.isDigit() || it == '+' }
        val encoded = Uri.encode(message)
        val url = if (cleanPhone.isNotEmpty()) {
            "https://wa.me/$cleanPhone?text=$encoded"
        } else {
            "https://wa.me/?text=$encoded"
        }
        openUrl(url)
    }

    fun launchSms(phone: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "SMS launch failed", e)
        }
    }

    fun launchEmail(to: String, subject: String, body: String) {
        try {
            val uri = Uri.parse("mailto:$to?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "Email launch failed", e)
        }
    }

    fun launchDialer(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "Dialer launch failed", e)
        }
    }

    fun launchMapsQuery(query: String) {
        try {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openUrl("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
        }
    }

    fun openUrl(url: String) {
        try {
            val normalized = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "URL launch failed", e)
        }
    }

    fun shareText(title: String, content: String) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            val chooser = Intent.createChooser(sendIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("VoiceAndAudioManager", "Share failed", e)
        }
    }

    fun shutdown() {
        stopCyberSynth()
        tts?.stop()
        tts?.shutdown()
    }
}
