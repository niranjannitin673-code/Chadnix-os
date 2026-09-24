package com.chadnix.os.core

import android.app.Application

/**
 * Chadnix - global application state.
 * Keeps track of which mode the user is currently running in
 * (Launcher / Home replacement, or Overlay / floating window manager).
 */
class ChadnixApp : Application() {

    companion object {
        lateinit var instance: ChadnixApp
            private set

        const val PREFS_NAME = "chadnix_prefs"
        const val KEY_MODE = "chadnix_mode"

        const val MODE_UNSET = 0
        const val MODE_LAUNCHER = 1
        const val MODE_OVERLAY = 2
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getSavedMode(): Int {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(KEY_MODE, MODE_UNSET)
    }

    fun saveMode(mode: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(KEY_MODE, mode).apply()
    }
}
