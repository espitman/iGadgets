package com.igadgets.app
import com.igadgets.app.R

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.Box
import androidx.glance.layout.Alignment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.RemoteViews
import androidx.glance.LocalContext

class ClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            ClockWidgetContent()
        }
    }

    @Composable
    private fun ClockWidgetContent() {
        val jalaliDate = JalaliCalendar.getJalaliDate()
        val remoteViews = RemoteViews(
            "com.igadgets.app",
            R.layout.widget_clock_native
        ).apply {
            val currentContext = LocalContext.current
            val jalaliBitmap = WidgetUtils.textToBitmap(
                context = currentContext,
                text = jalaliDate,
                color = android.graphics.Color.WHITE,
                sizeSp = 14f,
                isBold = true
            )
            setImageViewBitmap(R.id.jalali_date_img, jalaliBitmap)
        }
        
        // Use a Box with fillMaxSize and 0.dp padding to force edge-to-edge
        androidx.glance.layout.Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = androidx.glance.layout.Alignment.Center
        ) {
            AndroidRemoteViews(remoteViews)
        }
    }
}
