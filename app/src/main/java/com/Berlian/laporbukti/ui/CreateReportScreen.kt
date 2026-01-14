package com.Berlian.laporbukti.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.Berlian.laporbukti.data.ReportRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    repo: ReportRepository,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Fasilitas") }
    var description by remember { mutableStateOf("") }
    var photoUriString by remember { mutableStateOf<String?>(null) }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lon by remember { mutableStateOf<Double?>(null) }

    var locationMsg by remember { mutableStateOf<String?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    // --- Launchers (Sama seperti sebelumnya) ---
    val pickPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try { context.contentResolver.takePersistableUriPermission(uri, flag) } catch (e: Exception) {}
            photoUriString = uri.toString()
        }
    }

    fun getLocation() {
        isLoadingLocation = true
        locationMsg = "Mencari lokasi akurat..."
        val fused = LocationServices.getFusedLocationProviderClient(context)
        val cancellationToken = CancellationTokenSource()

        try {
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                .addOnSuccessListener { loc ->
                    isLoadingLocation = false
                    if (loc != null) {
                        lat = loc.latitude
                        lon = loc.longitude
                        locationMsg = "Lokasi terkunci: ${loc.latitude}, ${loc.longitude}"
                    } else {
                        locationMsg = "Gagal. Pastikan GPS aktif."
                    }
                }
                .addOnFailureListener {
                    isLoadingLocation = false
                    locationMsg = "Error: ${it.message}"
                }
        } catch (e: SecurityException) {
            isLoadingLocation = false
            locationMsg = "Izin ditolak."
        }
    }

    val locPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLocation()
        } else {
            locationMsg = "Izin lokasi diperlukan."
        }
    }

    // --- UI CONTENT ---
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Buat Laporan Baru", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Formulir
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Detail Masalah", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Laporan") },
                        leadingIcon = { Icon(Icons.Default.Title, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori") },
                        leadingIcon = { Icon(Icons.Default.Category, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Card Bukti & Lokasi
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Bukti & Lokasi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    // Preview Foto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUriString != null) {
                            AsyncImage(
                                model = photoUriString,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Tombol ganti kecil di pojok
                            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color.Black.copy(0.6f), RoundedCornerShape(8.dp)).padding(4.dp)) {
                                Text("Ganti Foto", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                                Text("Tap untuk tambah foto", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Tombol & Info Lokasi
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                if (fine) getLocation() else locPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                            },
                            enabled = !isLoadingLocation,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isLoadingLocation) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text("Mencari...")
                            } else {
                                Icon(Icons.Default.MyLocation, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Ambil Lokasi")
                            }
                        }
                    }

                    if (locationMsg != null) {
                        Text(
                            text = locationMsg!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (lat != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Tombol Simpan Besar
            Button(
                onClick = {
                    scope.launch {
                        val id = repo.add(title, description, category, photoUriString, lat, lon)
                        onSaved(id)
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kirim Laporan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
