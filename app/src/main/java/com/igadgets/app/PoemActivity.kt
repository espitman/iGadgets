package com.igadgets.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PoemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val poemId = intent.getIntExtra("POEM_ID", -1)
        Log.d("PoemActivity", "Started with poemId: $poemId")

        setContent {
            IGadgetsTheme {
                PoemDetailScreen(poemId, onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(poemId: Int, onBack: () -> Unit) {
    var poemContent by remember { mutableStateOf<GanjoorPoemResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(poemId) {
        if (poemId != -1) {
            try {
                Log.d("PoemActivity", "Fetching poem details for id: $poemId")
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.ganjoor.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PoemApiService::class.java)
                val response = withContext(Dispatchers.IO) { api.getPoemById(poemId) }
                poemContent = response
                Log.d("PoemActivity", "Poem fetched successfully")
            } catch (e: Exception) {
                Log.e("PoemActivity", "Error fetching poem", e)
                errorMessage = "خطا در دریافت اطلاعات از سرور"
            }
        } else {
            errorMessage = "شناسه شعر نامعتبر است"
            Log.e("PoemActivity", "Invalid poemId: $poemId")
        }
        isLoading = false
    }

    Scaffold(
        containerColor = Color(0xFF0a0f1a),
        topBar = {
            TopAppBar(
                title = { Text(poemContent?.poetName ?: "نمایش شعر") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1a1a2e),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF44ABFF))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = Color.White, textAlign = TextAlign.Center)
                }
            } else if (poemContent != null) {
                val content = poemContent!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = content.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = content.poetName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF44ABFF),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    items(content.verses.chunked(2)) { pair ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pair[0].text,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = nastaliqFamily,
                                fontSize = 24.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 38.sp
                            )
                            if (pair.size > 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pair[1].text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = nastaliqFamily,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 38.sp
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
