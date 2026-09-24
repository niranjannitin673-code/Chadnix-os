package com.chadnix.os.overlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.chadnix.os.R
import com.chadnix.os.core.AppRepository
import com.chadnix.os.ui.ModeChooserActivity

/**
 * Overlay mode: Chadnix runs as a floating taskbar drawn over every other app,
 * using SYSTEM_ALERT_WINDOW (TYPE_APPLICATION_OVERLAY). It does not replace the
 * home screen — the user keeps their normal launcher and Chadnix floats on top,
 * IceWM-taskbar style, with a drag handle and an app-drawer button.
 */
class ChadnixOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingTaskbar: View? = null
    private var drawerPanel: View? = null

    companion object {
        const val CHANNEL_ID = "chadnix_overlay_channel"
        const val NOTIF_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForegroundWithNotification()
        showFloatingTaskbar()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startForegroundWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Chadnix Overlay",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, ModeChooserActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Chadnix is running")
            .setContentText("Floating window manager active")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIF_ID, notification)
    }

    private fun showFloatingTaskbar() {
        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.x = 0
        params.y = 0

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.overlay_taskbar, null)
        floatingTaskbar = view

        // Drag-to-move handle, IceWM-taskbar-drag style
        var initialX = 0
        var initialY = 0
        var touchX = 0f
        var touchY = 0f

        view.findViewById<View>(R.id.overlayDragHandle).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX - (event.rawX - touchX).toInt()
                    params.y = initialY - (event.rawY - touchY).toInt()
                    windowManager.updateViewLayout(floatingTaskbar, params)
                    true
                }
                else -> false
            }
        }

        view.findViewById<View>(R.id.overlayDrawerButton).setOnClickListener {
            toggleAppDrawer()
        }

        view.findViewById<View>(R.id.overlayCloseButton).setOnClickListener {
            stopSelf()
        }

        windowManager.addView(floatingTaskbar, params)
    }

    private fun toggleAppDrawer() {
        if (drawerPanel != null) {
            windowManager.removeView(drawerPanel)
            drawerPanel = null
            return
        }

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.y = 60

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.overlay_app_drawer, null)
        drawerPanel = view

        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.overlayAppList)
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        val apps = AppRepository.getInstalledApps(this)
        recycler.adapter = OverlayAppListAdapter(apps) { app ->
            AppRepository.launchApp(this, app.packageName)
            toggleAppDrawer()
        }

        windowManager.addView(drawerPanel, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingTaskbar?.let { windowManager.removeView(it) }
        drawerPanel?.let { windowManager.removeView(it) }
    }
}
