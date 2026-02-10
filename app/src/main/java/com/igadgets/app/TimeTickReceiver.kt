package com.igadgets.app

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class TimeTickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_TICK) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context.packageName, ClockWidgetProvider::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                ClockWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }
}
