package com.igadgets.app

import com.igadgets.app.R

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews

class ClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        for (appWidgetId in appWidgetIds) {
            editor.remove("bg_color_$appWidgetId")
            editor.remove("text_color_$appWidgetId")
            editor.remove("opacity_$appWidgetId")
        }
        editor.apply()
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        
        // Get saved preferences or use defaults
        val bgColor = prefs.getInt("bg_color_$appWidgetId", Color.parseColor("#55000000"))
        val textColor = prefs.getInt("text_color_$appWidgetId", Color.WHITE)
        val opacity = prefs.getFloat("opacity_$appWidgetId", 0.33f)
        
        // Apply opacity to background color
        val finalBgColor = applyOpacity(bgColor, opacity)
        
        val jalaliDate = JalaliCalendar.getJalaliDate()
        
        val views = RemoteViews(context.packageName, R.layout.widget_clock_native).apply {
            // Render Jalali Date to Bitmap to ensure Vazirmatn font works
            val jalaliBitmap = WidgetUtils.textToBitmap(
                context = context,
                text = jalaliDate,
                color = Color.WHITE,
                sizeSp = 14f,
                isBold = true
            )
            setImageViewBitmap(R.id.jalali_date_img, jalaliBitmap)
            
            // Apply custom background color
            setInt(R.id.widget_background, "setBackgroundColor", finalBgColor)
            
            // Apply colors to other elements
            setTextColor(R.id.clock_hour, Color.WHITE)
            setTextColor(R.id.clock_minute, textColor)
            setTextColor(R.id.gregorian_date, Color.parseColor("#88FFFFFF"))
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun applyOpacity(color: Int, opacity: Float): Int {
        val alpha = (opacity * 255).toInt().coerceIn(0, 255)
        return Color.argb(
            alpha,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }
}
