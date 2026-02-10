package com.igadgets.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PoemWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_POEM = "com.igadgets.app.ACTION_REFRESH_POEM"
        private const val BASE_URL = "https://api.ganjoor.net/"
        
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        private val apiService = retrofit.create(PoemApiService::class.java)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_POEM) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PoemWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            
            CoroutineScope(Dispatchers.IO).launch {
                fetchAndStorePoem(context)
                for (id in ids) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val poemPrefs = context.getSharedPreferences("poem_prefs", Context.MODE_PRIVATE)
        val v1 = poemPrefs.getString("current_verse1", "در حال دریافت شعر...") ?: "در حال دریافت شعر..."
        val v2 = poemPrefs.getString("current_verse2", "") ?: ""
        val poetName = poemPrefs.getString("current_poet", "") ?: ""
        val poemId = poemPrefs.getInt("current_poem_id", -1)

        // Load widget specific settings
        val configPrefs = context.getSharedPreferences("poem_widget_prefs", Context.MODE_PRIVATE)
        val bgColor = configPrefs.getInt("bg_color_$appWidgetId", Color.BLACK)
        val textColor = configPrefs.getInt("text_color_$appWidgetId", Color.WHITE)
        val opacity = configPrefs.getFloat("opacity_$appWidgetId", 0.33f)
        val fontType = configPrefs.getString("font_$appWidgetId", "nastaliq") ?: "nastaliq"

        val views = RemoteViews(context.packageName, R.layout.widget_poem_layout).apply {
            // Apply Background with Opacity
            val alpha = (opacity * 255).toInt()
            val colorWithAlpha = Color.argb(alpha, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor))
            // Note: Since we use a rounded drawable as background, we need to be careful.
            // But setInt(..., "setBackgroundColor", ...) usually works or we can use a helper if needed.
            // Actually, widget_gradient_bg is a drawable. Changing its color is hard.
            // Better to use a solid color for simplicity or keep the gradient but tinted.
            // For now, let's use setInt to set the background color of the root.
            setInt(R.id.poem_widget_root, "setBackgroundColor", colorWithAlpha)

            // Setup Font correctly
            val fontRes = if (fontType == "nastaliq") R.font.nastaliq else R.font.vazirmatn_bold
            val fontSize = if (fontType == "nastaliq") 24f else 17f

            // Verse 1
            val bitmapV1 = WidgetUtils.textToBitmap(context, v1, textColor, fontSize, true, fontRes)
            setImageViewBitmap(R.id.verse1_img, bitmapV1)

            // Verse 2
            if (v2.isNotEmpty()) {
                val bitmapV2 = WidgetUtils.textToBitmap(context, v2, textColor, fontSize, true, fontRes)
                setImageViewBitmap(R.id.verse2_img, bitmapV2)
            } else {
                setImageViewBitmap(R.id.verse2_img, null)
            }

            // Poet Name
            if (poetName.isNotEmpty()) {
                val bitmapPoet = WidgetUtils.textToBitmap(context, poetName, Color.parseColor("#44ABFF"), 14f, true, R.font.vazirmatn_bold)
                setImageViewBitmap(R.id.poet_name_img, bitmapPoet)
            }

            // Setup Refresh Button
            val refreshIntent = Intent(context, PoemWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_POEM
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)

            // Setup View Button
            val clickIntent = Intent(context, PoemActivity::class.java).apply {
                putExtra("POEM_ID", poemId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val clickPendingIntent = PendingIntent.getActivity(
                context, appWidgetId, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.view_poem_button, clickPendingIntent)
            
            // Also keep click on root for accessibility
            setOnClickPendingIntent(R.id.poem_widget_root, clickPendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
        
        // If data is empty, fetch it
        if (v1 == "در حال دریافت شعر...") {
            CoroutineScope(Dispatchers.IO).launch {
                fetchAndStorePoem(context)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private suspend fun fetchAndStorePoem(context: Context) {
        try {
            val response = apiService.getRandomPoem()
            val v1 = if (response.verses.isNotEmpty()) response.verses[0].text else ""
            val v2 = if (response.verses.size >= 2) response.verses[1].text else ""

            context.getSharedPreferences("poem_prefs", Context.MODE_PRIVATE).edit().apply {
                putString("current_verse1", v1)
                putString("current_verse2", v2)
                putString("current_poet", response.poetName)
                putInt("current_poem_id", response.id)
                apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
