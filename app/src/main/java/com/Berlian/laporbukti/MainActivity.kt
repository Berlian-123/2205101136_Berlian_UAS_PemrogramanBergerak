package com.Berlian.laporbukti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.Berlian.laporbukti.data.AppDatabase
import com.Berlian.laporbukti.data.ReportRepository
import com.Berlian.laporbukti.navigation.AppNav
import com.Berlian.laporbukti.ui.theme.LaporBuktiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup Database & Repo
        val database = AppDatabase.getDatabase(applicationContext)
        val repo = ReportRepository(database.reportDao())

        setContent {
            LaporBuktiTheme {
                AppNav(repo)
            }
        }
    }
}
