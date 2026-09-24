package com.chadnix.os.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.chadnix.os.R
import com.chadnix.os.core.ChadnixApp
import com.chadnix.os.databinding.ActivityModeChooserBinding
import com.chadnix.os.launcher.LauncherActivity
import com.chadnix.os.overlay.ChadnixOverlayService

/**
 * First screen the user sees. Chadnix supports two modes:
 *  - LAUNCHER: replaces the Android home screen (full IceWM-style desktop)
 *  - OVERLAY: floats on top of whatever app is open (taskbar + window switcher),
 *             without taking over the home screen
 *
 * The user picks one; the choice is remembered but can be changed again from here.
 */
class ModeChooserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModeChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLauncherMode.setOnClickListener {
            (application as ChadnixApp).saveMode(ChadnixApp.MODE_LAUNCHER)
            promptSetDefaultLauncher()
        }

        binding.btnOverlayMode.setOnClickListener {
            (application as ChadnixApp).saveMode(ChadnixApp.MODE_OVERLAY)
            requestOverlayPermissionThenStart()
        }
    }

    private fun promptSetDefaultLauncher() {
        // Sends the user to Android's "select default home app" chooser.
        // Chadnix's LauncherActivity is already registered with the HOME category
        // in the manifest, so it will appear in this list.
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        startActivity(intent)
        finish()
    }

    private fun requestOverlayPermissionThenStart() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            // User must grant permission, then come back and tap Overlay mode again.
            return
        }
        val serviceIntent = Intent(this, ChadnixOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        finish()
    }
}
