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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_bold, FontWeight.Bold)
)

class WidgetConfigActivity : ComponentActivity() {
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

        val prefs = getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val currentBgColor = prefs.getInt("bg_color_$appWidgetId", android.graphics.Color.BLACK)
        val currentMinuteColor = prefs.getInt("text_color_$appWidgetId", android.graphics.Color.parseColor("#44ABFF"))
        val currentOpacity = prefs.getFloat("opacity_$appWidgetId", 0.33f)

        setContent {
            IGadgetsTheme {
                WidgetConfigScreen(
                    initialBgColor = Color(currentBgColor),
                    initialMinuteColor = Color(currentMinuteColor),
                    initialOpacity = currentOpacity,
                    onSave = { bgColor, textColor, opacity ->
                        saveWidgetPreferences(bgColor, textColor, opacity)
                        updateWidget()
                        finish()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun saveWidgetPreferences(bgColor: Color, textColor: Color, opacity: Float) {
        val prefs = getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("bg_color_$appWidgetId", bgColor.toArgb())
            putInt("text_color_$appWidgetId", textColor.toArgb())
            putFloat("opacity_$appWidgetId", opacity)
            apply()
        }
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        ClockWidgetProvider().onUpdate(
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
fun WidgetConfigScreen(
    initialBgColor: Color,
    initialMinuteColor: Color,
    initialOpacity: Float,
    onSave: (Color, Color, Float) -> Unit,
    onCancel: () -> Unit
) {
    var backgroundColor by remember { mutableStateOf(initialBgColor) }
    var minuteColor by remember { mutableStateOf(initialMinuteColor) }
    var opacity by remember { mutableFloatStateOf(initialOpacity) }
    var showBgColorPicker by remember { mutableStateOf(false) }
    var showMinuteColorPicker by remember { mutableStateOf(false) }

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
                text = "تنظیمات ویجت",
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                fontFamily = vazirmatn,
                textAlign = TextAlign.End
            )

            // Background Color Picker
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("رنگ پس‌زمینه", color = Color.White, fontSize = 16.sp, fontFamily = vazirmatn)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        // Custom color button
                        Button(
                            onClick = { showBgColorPicker = true },
                            modifier = Modifier.size(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2a2a3e)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("🎨", fontSize = 20.sp)
                        }
                        
                        ColorButton(Color(0xFF2d1b4e), backgroundColor == Color(0xFF2d1b4e)) {
                            backgroundColor = Color(0xFF2d1b4e)
                        }
                        ColorButton(Color(0xFF0a2540), backgroundColor == Color(0xFF0a2540)) {
                            backgroundColor = Color(0xFF0a2540)
                        }
                        ColorButton(Color(0xFF1a1a2e), backgroundColor == Color(0xFF1a1a2e)) {
                            backgroundColor = Color(0xFF1a1a2e)
                        }
                        ColorButton(Color.Black, backgroundColor == Color.Black) {
                            backgroundColor = Color.Black
                        }
                    }
                }
            }

            // Minute Color Picker
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("رنگ دقیقه", color = Color.White, fontSize = 16.sp, fontFamily = vazirmatn)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        // Custom color button
                        Button(
                            onClick = { showMinuteColorPicker = true },
                            modifier = Modifier.size(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2a2a3e)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("🎨", fontSize = 20.sp)
                        }
                        
                        ColorButton(Color(0xFFFF6B9D), minuteColor == Color(0xFFFF6B9D)) {
                            minuteColor = Color(0xFFFF6B9D)
                        }
                        ColorButton(Color(0xFFFFD700), minuteColor == Color(0xFFFFD700)) {
                            minuteColor = Color(0xFFFFD700)
                        }
                        ColorButton(Color(0xFF44ABFF), minuteColor == Color(0xFF44ABFF)) {
                            minuteColor = Color(0xFF44ABFF)
                        }
                        ColorButton(Color.White, minuteColor == Color.White) {
                            minuteColor = Color.White
                        }
                    }
                }
            }

            // Opacity Slider
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("شفافیت پس‌زمینه: ${(opacity * 100).toInt()}%", 
                        color = Color.White, fontSize = 16.sp, fontFamily = vazirmatn)
                    Spacer(modifier = Modifier.height(12.dp))
                    
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
            }

            // Preview
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("پیش‌نمایش", color = Color.White, fontSize = 16.sp, fontFamily = vazirmatn)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Simulated phone wallpaper with widget
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667eea),
                                        Color(0xFF764ba2),
                                        Color(0xFF2d1b4e),
                                        Color(0xFF0a0f1a)
                                    ),
                                    center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f),
                                    radius = 800f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Widget preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    backgroundColor.copy(alpha = opacity),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left side - Clock
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "13",
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = vazirmatn
                                    )
                                    Text(
                                        text = ":",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 28.sp
                                    )
                                    Text(
                                        text = "29",
                                        color = minuteColor,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = vazirmatn
                                    )
                                }
                                
                                // Right side - Dates
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "دوشنبه، 13 بهمن 1404",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = vazirmatn
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Feb 2, 2026",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 9.sp,
                                        fontFamily = vazirmatn
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "لغو", 
                        fontFamily = vazirmatn, 
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                Button(
                    onClick = { onSave(backgroundColor, minuteColor, opacity) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF44ABFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "ذخیره", 
                        fontFamily = vazirmatn, 
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    // Background Color Picker Dialog
    if (showBgColorPicker) {
        ColorPickerDialog(
            currentColor = backgroundColor,
            onColorSelected = { 
                backgroundColor = it
                showBgColorPicker = false
            },
            onDismiss = { showBgColorPicker = false }
        )
    }
    
    // Minute Color Picker Dialog
    if (showMinuteColorPicker) {
        ColorPickerDialog(
            currentColor = minuteColor,
            onColorSelected = { 
                minuteColor = it
                showMinuteColorPicker = false
            },
            onDismiss = { showMinuteColorPicker = false }
        )
    }
}

@Composable
fun ColorPickerDialog(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember { mutableFloatStateOf(currentColor.red) }
    var green by remember { mutableFloatStateOf(currentColor.green) }
    var blue by remember { mutableFloatStateOf(currentColor.blue) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("انتخاب رنگ دلخواه", fontFamily = vazirmatn) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(red, green, blue), RoundedCornerShape(8.dp))
                )
                
                // Red Slider
                Column {
                    Text("قرمز: ${(red * 255).toInt()}", fontSize = 14.sp, fontFamily = vazirmatn)
                    Slider(
                        value = red,
                        onValueChange = { red = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red
                        )
                    )
                }
                
                // Green Slider
                Column {
                    Text("سبز: ${(green * 255).toInt()}", fontSize = 14.sp, fontFamily = vazirmatn)
                    Slider(
                        value = green,
                        onValueChange = { green = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Green,
                            activeTrackColor = Color.Green
                        )
                    )
                }
                
                // Blue Slider
                Column {
                    Text("آبی: ${(blue * 255).toInt()}", fontSize = 14.sp, fontFamily = vazirmatn)
                    Slider(
                        value = blue,
                        onValueChange = { blue = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Blue,
                            activeTrackColor = Color.Blue
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onColorSelected(Color(red, green, blue)) },
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF44ABFF)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text("تایید", fontFamily = vazirmatn)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(50.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text("لغو", fontFamily = vazirmatn)
            }
        }
    )
}

@Composable
fun ColorButton(color: Color, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (selected) {
            Text("✓", color = Color.White, fontSize = 20.sp)
        }
    }
}
