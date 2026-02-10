package com.igadgets.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IGadgetsTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        containerColor = Color(0xFF0a0f1a)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Header
                Text(
                    text = "iGadgets",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = "مجموعه ابزارک‌های حرفه‌ای",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF88FFFFFF),
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Widget Cards...
            item {
                WidgetCard(
                    title = "ساعت دیجیتال",
                    description = "ساعت مدرن با تاریخ شمسی و میلادی",
                    icon = Icons.Default.AccessTimeFilled,
                    status = "فعال و آماده استفاده"
                )
            }
            
            item {
                WidgetCard(
                    title = "تک‌بیت روز",
                    description = "گلچین اشعار فارسی از گنجور با بروزرسانی ساعتی",
                    icon = Icons.Default.CalendarMonth,
                    status = "فعال و آماده استفاده",
                    isComingSoon = false
                )
            }
            
            item {
                WidgetCard(
                    title = "هواشناسی",
                    description = "وضعیت جوی با گرافیک خیره‌کننده",
                    icon = Icons.Default.WbSunny,
                    status = "به‌زودی",
                    isComingSoon = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1a1a2e).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "📱 راهنمای افزودن ویجت",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "۱. روی صفحه اصلی انگشت خود را نگه دارید\n" +
                                  "۲. گزینه Widgets را انتخاب کنید\n" +
                                  "۳. برنامه iGadgets را پیدا کنید\n" +
                                  "۴. ویجت مورد نظر را به صفحه بکشید",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFCCCCCC),
                            lineHeight = 26.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun WidgetCard(
    title: String,
    description: String,
    icon: ImageVector,
    status: String,
    isComingSoon: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isComingSoon) {
                            listOf(Color(0xFF1a1a2e), Color(0xFF0f1419))
                        } else {
                            listOf(Color(0xFF1a1a2e), Color(0xFF0a2540))
                        }
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isComingSoon) Color(0xFF2a2a3e) else Color(0xFF44ABFF).copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isComingSoon) Color(0xFF666666) else Color(0xFF44ABFF),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Text Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isComingSoon) Color(0xFF888888) else Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isComingSoon) Color(0xFF666666) else Color(0xFF44ABFF)
                    )
                }
            }
        }
    }
}
