package com.chadnix.os.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * Loads the list of launchable apps on the device.
 * Used by both Launcher mode (full home screen grid) and Overlay mode (floating app drawer).
 */
object AppRepository {

    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        return resolveInfos
            .map { resolveInfo ->
                AppInfo(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.loadIcon(pm)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(context: Context, packageName: String) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}
