package com.Berlian.laporbukti.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.Berlian.laporbukti.data.ReportRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repo: ReportRepository,
    onBack: () -> Unit
) {
    val reportsList by repo.getAll().collectAsState(initial = emptyList())

    val total = reportsList.size
    val draftCount = reportsList.count { it.status == "DRAFT" }
    val sentCount = reportsList.count { it.status == "SENT" }
    val doneCount = reportsList.count { it.status == "DONE" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik Laporan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (total == 0) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Belum ada data laporan.")
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
                PieChart(
                    draft = draftCount.toFloat(),
                    sent = sentCount.toFloat(),
                    done = doneCount.toFloat(),
                    total = total.toFloat()
                )

                Spacer(modifier = Modifier.height(40.dp))

                StatItem(label = "Draft (Belum Dikirim)", count = draftCount, color = Color.Gray)
                StatItem(label = "Terkirim (Menunggu)", count = sentCount, color = Color(0xFF1565C0))
                StatItem(label = "Selesai (Dikerjakan)", count = doneCount, color = Color(0xFF2E7D32))

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Total Laporan Masuk: $total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PieChart(draft: Float, sent: Float, done: Float, total: Float) {
    val draftAngle = (draft / total) * 360f
    val sentAngle = (sent / total) * 360f
    val doneAngle = (done / total) * 360f

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = -90f

        // Draft
        drawArc(color = Color.Gray, startAngle = startAngle, sweepAngle = draftAngle, useCenter = true, size = Size(size.width, size.height))
        startAngle += draftAngle

        // Sent
        drawArc(color = Color(0xFF1565C0), startAngle = startAngle, sweepAngle = sentAngle, useCenter = true, size = Size(size.width, size.height))
        startAngle += sentAngle

        // Done
        drawArc(color = Color(0xFF2E7D32), startAngle = startAngle, sweepAngle = doneAngle, useCenter = true, size = Size(size.width, size.height))

        drawCircle(color = Color.White, radius = size.width / 2, style = Stroke(width = 4f))
    }
}

@Composable
fun StatItem(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = color, shape = CircleShape, modifier = Modifier.size(16.dp)) {}
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f))
        Text("$count", fontWeight = FontWeight.Bold)
    }
}
