package com.mackenzie.downhub.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.ui.main.videohub.common.removeVersionSuffix
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Wrapper around Firebase Remote Config for version checking.
 *
 * Usage from a coroutine:
 * ```
 * RemoteConfigHelper.init()
 * RemoteConfigHelper.fetchAndActivate()
 * val latest = RemoteConfigHelper.latestServerVersion
 * ```
 */
object RemoteConfigHelper {

    private const val TAG = "RemoteConfigHelper"

    /** Remote Config parameter key for the latest published version. */
    const val KEY_LATEST_SERVER_VERSION = "latest_server_version"

    /** Default value used until the first successful fetch. */
    private const val DEFAULT_LATEST_VERSION = "0.0.0"

    private val remoteConfig: FirebaseRemoteConfig
        get() = Firebase.remoteConfig

    /** Cached latest version; updated after every successful fetch + activate. */
    val latestServerVersion: String
        get() = remoteConfig.getString(KEY_LATEST_SERVER_VERSION)

    /**
     * Configures Remote Config settings and sets in-app defaults.
     * Safe to call multiple times.
     */
    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour in production
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf<String, Any>(
            KEY_LATEST_SERVER_VERSION to DEFAULT_LATEST_VERSION,
        )
        remoteConfig.setDefaultsAsync(defaults)

        Log.d(TAG, "Remote Config initialized with defaults")
    }

    /**
     * Fetches latest values from the backend and activates them.
     *
     * @return `true` if fetched values were activated (i.e. they differ from
     *         the previously activated set), `false` otherwise.
     */
    suspend fun fetchAndActivate(): Boolean = suspendCancellableCoroutine { cont ->
        remoteConfig.fetchAndActivate()
            .addOnSuccessListener { activated ->
                Log.d(TAG, "fetchAndActivate success — activated=$activated, latestVersion=$latestServerVersion")
                cont.resume(activated)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "fetchAndActivate failed", exception)
                cont.resumeWithException(exception)
            }
    }
}
