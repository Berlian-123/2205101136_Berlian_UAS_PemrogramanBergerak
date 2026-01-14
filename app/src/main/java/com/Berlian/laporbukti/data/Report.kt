package com.Berlian.laporbukti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val photoUri: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String = "DRAFT",
    val createdAt: Long = System.currentTimeMillis()
)
