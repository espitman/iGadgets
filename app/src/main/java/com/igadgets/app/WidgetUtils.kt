package com.igadgets.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

object WidgetUtils {
    fun textToBitmap(
        context: Context,
        text: String,
        color: Int,
        sizeSp: Float,
        isBold: Boolean = false,
        fontResId: Int? = null
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = sizeSp * context.resources.displayMetrics.density
        
        val actualFontResId = fontResId ?: if (isBold) R.font.vazirmatn_bold else R.font.vazirmatn_regular
        val typeface = ResourcesCompat.getFont(context, actualFontResId)
        paint.typeface = typeface
        
        paint.textAlign = Paint.Align.LEFT
        val baseline = -paint.ascent()
        val width = (paint.measureText(text) + 0.5f).toInt()
        val height = (baseline + paint.descent() + 0.5f).toInt()
        
        val image = Bitmap.createBitmap(if (width > 0) width else 1, if (height > 0) height else 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text, 0f, baseline, paint)
        return image
    }
}
