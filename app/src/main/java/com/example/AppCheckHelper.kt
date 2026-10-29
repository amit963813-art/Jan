package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"

private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun ensureFirebaseInitialized(context: Context) {
    val appContext = context.applicationContext ?: context
    if (FirebaseApp.getApps(appContext).isNotEmpty()) return

    // 1. Try standard initialization from google-services.json generated resources
    FirebaseApp.initializeApp(appContext)
    if (FirebaseApp.getApps(appContext).isNotEmpty()) return

    // 2. Fallback: initialize explicitly from firebase_applet_config.xml resources
    try {
        val res = appContext.resources
        val pkg = appContext.packageName
        val appIdRes = res.getIdentifier("fb_fallback_app_id", "string", pkg)
        val apiKeyRes = res.getIdentifier("fb_fallback_api_key", "string", pkg)
        val projectIdRes = res.getIdentifier("fb_fallback_project_id", "string", pkg)
        val senderIdRes = res.getIdentifier("fb_fallback_sender_id", "string", pkg)
        val bucketRes = res.getIdentifier("fb_fallback_storage_bucket", "string", pkg)

        if (appIdRes != 0 && apiKeyRes != 0 && projectIdRes != 0) {
            val options = FirebaseOptions.Builder()
                .setApplicationId(res.getString(appIdRes))
                .setApiKey(res.getString(apiKeyRes))
                .setProjectId(res.getString(projectIdRes))
                .apply {
                    if (senderIdRes != 0) setGcmSenderId(res.getString(senderIdRes))
                    if (bucketRes != 0) setStorageBucket(res.getString(bucketRes))
                }
                .build()
            FirebaseApp.initializeApp(appContext, options)
            Log.d(TAG, "FirebaseApp initialized via fallback options.")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Fallback FirebaseApp initialization failed", e)
    }
}

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        ensureFirebaseInitialized(context)

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)

            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}
