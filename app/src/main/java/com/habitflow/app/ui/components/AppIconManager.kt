package com.habitflow.app.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color

enum class AppIcon(
    val alias: String,
    val label: String,
    val previewBg: Color,
    val previewFg: Color?   // null = show the Bun logo image
) {
    BUN("IconBun", "Bun", Color(0xFFFBF6E9), null),
    SUNSET("IconPenguin", "Penguin", Color(0xFFFBF6E9),null),
    MIDNIGHT("IconGIF", "GIF", Color(0xFFFBF6E9), null)
}

object AppIconManager {

    private fun component(context: Context, icon: AppIcon) =
        ComponentName(context.packageName, "com.habitflow.app.${icon.alias}")

    private fun isEnabled(context: Context, icon: AppIcon): Boolean {
        return when (context.packageManager.getComponentEnabledSetting(component(context, icon))) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
            else -> icon == AppIcon.BUN   // manifest default
        }
    }

    fun current(context: Context): AppIcon =
        AppIcon.entries.firstOrNull { isEnabled(context, it) } ?: AppIcon.BUN

    fun apply(context: Context, icon: AppIcon) {
        if (icon == current(context)) return
        val pm = context.packageManager
        // Enable the new one first so there's never a moment with no launcher icon
        pm.setComponentEnabledSetting(
            component(context, icon),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        AppIcon.entries.filter { it != icon }.forEach {
            pm.setComponentEnabledSetting(
                component(context, it),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}