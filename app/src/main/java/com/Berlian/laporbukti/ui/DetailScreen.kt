package com.Berlian.laporbukti.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.Berlian.laporbukti.data.Report
import com.Berlian.laporbukti.data.ReportRepository
import com.Berlian.laporbukti.data.UserSession
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    repo: ReportRepository,
    reportId: Long,
    onBack: () -> Unit
) {
    var report by remember { mutableStateOf<Report?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Load data
    LaunchedEffect(reportId) {
        report = repo.getById(reportId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Detail Laporan", style = MaterialTheme.typography.headlineSmall)

        if (report == null) {
            Text("Memuat data...", style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = onBack) { Text("Kembali") }
        } else {
            val r = report!!

            // --- Info Utama ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = r.title, style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    Text("Kategori: ${r.category}", style = MaterialTheme.typography.labelLarge)

                    // Warna status beda-beda
                    val statusColor = when(r.status) {
                        "DONE" -> Color(0xFF2E7D32) // Hijau
                        "SENT" -> Color(0xFF1565C0) // Biru
                        else -> Color.Gray
                    }
                    Text("Status: ${r.status}", style = MaterialTheme.typography.labelLarge, color = statusColor)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Deskripsi:", style = MaterialTheme.typography.titleSmall)
                    Text(r.description, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // --- Foto ---
            if (r.photoUri != null) {
                Text("Foto Bukti:", style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    AsyncImage(
                        model = r.photoUri,
                        contentDescription = "Foto Bukti",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // --- Lokasi & Maps ---
            if (r.latitude != null && r.longitude != null) {
                Text("Lokasi Kejadian:", style = MaterialTheme.typography.titleMedium)
                Text("Lat: ${r.latitude}, Lon: ${r.longitude}", style = MaterialTheme.typography.bodySmall)

                Button(
                    onClick = {
                        val uri = Uri.parse("geo:${r.latitude},${r.longitude}?q=${r.latitude},${r.longitude}(${r.title})")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${r.latitude},${r.longitude}"))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Lihat di Google Maps") }
            }

            HorizontalDivider()

            // --- PANEL ADMIN / FEEDBACK USER ---
            if (UserSession.isAdmin()) {
                Text("Panel Admin:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (r.status == "DRAFT") {
                        Button(
                            onClick = { scope.launch { repo.updateStatus(r.id, "SENT"); report = repo.getById(r.id) } },
                            modifier = Modifier.weight(1f)
                        ) { Text("Terima Laporan") }
                    } else if (r.status == "SENT") {
                        Button(
                            onClick = { scope.launch { repo.updateStatus(r.id, "DONE"); report = repo.getById(r.id) } },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) { Text("Selesaikan") }
                    }
                }
            } else {
                // Tampilan Warga
                if (r.status == "DONE") {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                        Text(
                            "✅ Laporan ini telah diselesaikan petugas.",
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            color = Color(0xFF1B5E20)
                        )
                    }
                } else if (r.status == "SENT") {
                    Text("ℹ️ Sedang diproses oleh petugas.", color = Color(0xFF1565C0))
                }
            }

            // --- Tombol Navigasi Bawah ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Kembali") }

                // Hapus (Bisa diakses siapa saja atau mau dibatasi admin juga boleh)
                OutlinedButton(
                    onClick = { scope.launch { repo.delete(r.id); onBack() } },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) { Text("Hapus") }
            }
        }
    }
}
