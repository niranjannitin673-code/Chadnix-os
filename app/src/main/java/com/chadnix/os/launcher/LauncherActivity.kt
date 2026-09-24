package com.chadnix.os.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chadnix.os.core.AppRepository
import com.chadnix.os.databinding.ActivityLauncherBinding

/**
 * Chadnix Launcher mode: acts as the Android HOME screen.
 * IceWM-style layout: app grid fills the screen, a taskbar sits at the bottom
 * with a "start menu" button and space reserved for running-task entries.
 */
class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private lateinit var appAdapter: AppGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppGrid()
        setupTaskbar()
    }

    private fun setupAppGrid() {
        val apps = AppRepository.getInstalledApps(this)
        appAdapter = AppGridAdapter(apps) { app ->
            AppRepository.launchApp(this, app.packageName)
        }
        binding.appGrid.layoutManager = GridLayoutManager(this, 4)
        binding.appGrid.adapter = appAdapter
    }

    private fun setupTaskbar() {
        binding.btnStartMenu.setOnClickListener {
            // Toggle app grid visibility / start menu, IceWM-style
            binding.appGrid.visibility =
                if (binding.appGrid.visibility == android.view.View.VISIBLE)
                    android.view.View.GONE
                else
                    android.view.View.VISIBLE
        }
    }

    // Pressing back on the home screen should do nothing (like a real launcher)
    override fun onBackPressed() {
        // no-op
    }
}
