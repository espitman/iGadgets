package com.igadgets.app

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PoemWidgetConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setResult(Activity.RESULT_CANCELED)
        
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val prefs = getSharedPreferences("poem_widget_prefs", Context.MODE_PRIVATE)
        val currentBgColor = prefs.getInt("bg_color_$appWidgetId", android.graphics.Color.BLACK)
        val currentTextColor = prefs.getInt("text_color_$appWidgetId", android.graphics.Color.WHITE)
        val currentOpacity = prefs.getFloat("opacity_$appWidgetId", 0.33f)
        val currentFont = prefs.getString("font_$appWidgetId", "nastaliq") ?: "nastaliq"

        setContent {
            IGadgetsTheme {
                PoemWidgetConfigScreen(
                    initialBgColor = Color(currentBgColor),
                    initialTextColor = Color(currentTextColor),
                    initialOpacity = currentOpacity,
                    initialFont = currentFont,
                    onSave = { bgColor, textColor, opacity, font ->
                        saveWidgetPreferences(bgColor, textColor, opacity, font)
                        updateWidget()
                        finish()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun saveWidgetPreferences(bgColor: Color, textColor: Color, opacity: Float, font: String) {
        val prefs = getSharedPreferences("poem_widget_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("bg_color_$appWidgetId", bgColor.toArgb())
            putInt("text_color_$appWidgetId", textColor.toArgb())
            putFloat("opacity_$appWidgetId", opacity)
            putString("font_$appWidgetId", font)
            apply()
        }
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        PoemWidgetProvider().onUpdate(
            this,
            appWidgetManager,
            intArrayOf(appWidgetId)
        )
        
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
    }
}

@Composable
fun PoemWidgetConfigScreen(
    initialBgColor: Color,
    initialTextColor: Color,
    initialOpacity: Float,
    initialFont: String,
    onSave: (Color, Color, Float, String) -> Unit,
    onCancel: () -> Unit
) {
    var backgroundColor by remember { mutableStateOf(initialBgColor) }
    var textColor by remember { mutableStateOf(initialTextColor) }
    var opacity by remember { mutableFloatStateOf(initialOpacity) }
    var font by remember { mutableStateOf(initialFont) }
    
    var showBgColorPicker by remember { mutableStateOf(false) }
    var showTextColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0a0f1a)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "تنظیمات ویجت شعر",
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = vazirmatnFamily,
                textAlign = TextAlign.End
            )

            // Background Color Picker
            ConfigSection(title = "رنگ پس‌زمینه") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    IconButton(
                        onClick = { showBgColorPicker = true },
                        modifier = Modifier.size(56.dp).background(Color(0xFF2a2a3e), RoundedCornerShape(12.dp))
                    ) {
                        Text("🎨", fontSize = 20.sp)
                    }
                    ColorButton(Color(0xFF2d1b4e), backgroundColor == Color(0xFF2d1b4e)) { backgroundColor = Color(0xFF2d1b4e) }
                    ColorButton(Color(0xFF0a2540), backgroundColor == Color(0xFF0a2540)) { backgroundColor = Color(0xFF0a2540) }
                    ColorButton(Color.Black, backgroundColor == Color.Black) { backgroundColor = Color.Black }
                }
            }

            // Text Color Picker
            ConfigSection(title = "رنگ متن") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    IconButton(
                        onClick = { showTextColorPicker = true },
                        modifier = Modifier.size(56.dp).background(Color(0xFF2a2a3e), RoundedCornerShape(12.dp))
                    ) {
                        Text("🎨", fontSize = 20.sp)
                    }
                    ColorButton(Color(0xFF44ABFF), textColor == Color(0xFF44ABFF)) { textColor = Color(0xFF44ABFF) }
                    ColorButton(Color(0xFFFFD700), textColor == Color(0xFFFFD700)) { textColor = Color(0xFFFFD700) }
                    ColorButton(Color.White, textColor == Color.White) { textColor = Color.White }
                }
            }

            // Opacity Slider
            ConfigSection(title = "شفافیت: ${(opacity * 100).toInt()}%") {
                Slider(
                    value = opacity,
                    onValueChange = { opacity = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF44ABFF),
                        activeTrackColor = Color(0xFF44ABFF)
                    )
                )
            }

            // Font Selection
            ConfigSection(title = "انتخاب فونت") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    ChoiceButton(
                        text = "وزیر",
                        isSelected = font == "vazir",
                        onClick = { font = "vazir" }
                    )
                    ChoiceButton(
                        text = "نستعلیق",
                        isSelected = font == "nastaliq",
                        onClick = { font = "nastaliq" }
                    )
                }
            }

            // Preview
            ConfigSection(title = "پیش‌نمایش") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            brush = Brush.verticalGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor.copy(alpha = opacity), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "خوشا آنان که الله یارشان بی",
                                color = textColor,
                                fontSize = if (font == "nastaliq") 22.sp else 16.sp,
                                fontFamily = if (font == "nastaliq") nastaliqFamily else vazirmatnFamily,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "باباطاهر",
                                color = Color(0xFF44ABFF),
                                fontSize = 12.sp,
                                fontFamily = vazirmatnFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("لغو", fontFamily = vazirmatnFamily)
                }
                Button(
                    onClick = { onSave(backgroundColor, textColor, opacity, font) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF44ABFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ذخیره", fontFamily = vazirmatnFamily)
                }
            }
        }
    }

    if (showBgColorPicker) {
        ColorPickerDialog(
            currentColor = backgroundColor,
            onColorSelected = { backgroundColor = it; showBgColorPicker = false },
            onDismiss = { showBgColorPicker = false }
        )
    }
    if (showTextColorPicker) {
        ColorPickerDialog(
            currentColor = textColor,
            onColorSelected = { textColor = it; showTextColorPicker = false },
            onDismiss = { showTextColorPicker = false }
        )
    }
}

@Composable
fun ConfigSection(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(title, color = Color.White, fontSize = 16.sp, fontFamily = vazirmatnFamily)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ChoiceButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF44ABFF) else Color(0xFF2a2a3e)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(48.dp).padding(horizontal = 4.dp)
    ) {
        // Apply appropriate font for buttons
        Text(text, fontFamily = if(text == "نستعلیق") nastaliqFamily else vazirmatnFamily, color = Color.White)
    }
}
