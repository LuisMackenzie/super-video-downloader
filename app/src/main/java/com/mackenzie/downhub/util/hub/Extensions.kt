package com.mackenzie.downhub.util.hub

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit

fun Activity.setExternalPlayerMode(modeExternal: Boolean) {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    requireNotNull(sharedPref)
    sharedPref.edit {
        putBoolean("external_player_mode", modeExternal)
    }
    Log.v("SetMode", "SET::modeExternal=${modeExternal}")
}

fun Activity.getExternalPlayerMode(): Boolean {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    val modeExternal = sharedPref.getBoolean("external_player_mode", false)
    Log.v("GetMode", "GET::modeExternal=${modeExternal}")
    return modeExternal
}