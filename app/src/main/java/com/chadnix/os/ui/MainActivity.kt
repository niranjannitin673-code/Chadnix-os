package com.chadnix.os.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.chadnix.os.databinding.ActivityMainBinding
import com.chadnix.os.overlay.ChadnixOverlayService

/**
 * Chadnix is now overlay-only: no Home/Launcher replacement mode.
 * This screen just requests the overlay permission (if not already granted)
 * and starts the floating taskbar service.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateStatusText()

        binding.btnStart.setOnClickListener {
            requestOverlayPermissionThenStart()
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, ChadnixOverlayService::class.java))
            updateStatusText()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusText()
    }

    private fun updateStatusText() {
        binding.statusText.text = if (Settings.canDrawOverlays(this)) {
            "Overlay permission: granted\nTap Start to launch the Chadnix taskbar."
        } else {
            "Overlay permission: not granted yet.\nTap Start to grant it."
        }
    }

    private fun requestOverlayPermissionThenStart() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            // User must grant permission (and on Android 13+, also allow restricted
            // settings if the APK was sideloaded), then come back and tap Start again.
            return
        }
        val serviceIntent = Intent(this, ChadnixOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
