package com.Berlian.laporbukti.data

import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {

    fun getAll(): Flow<List<Report>> = reportDao.getAllReports()

    fun search(query: String): Flow<List<Report>> {
        return reportDao.searchReports(query)
    }

    suspend fun getById(id: Long): Report? = reportDao.getReportById(id)

    suspend fun add(
        title: String,
        description: String,
        category: String,
        photoUri: String?,
        lat: Double?,
        lon: Double?
    ): Long {
        val report = Report(
            title = title,
            description = description,
            category = category,
            photoUri = photoUri,
            latitude = lat,
            longitude = lon
        )
        return reportDao.insert(report)
    }

    suspend fun updateStatus(id: Long, status: String) {
        val report = getById(id)
        if (report != null) {
            reportDao.update(report.copy(status = status))
        }
    }

    suspend fun delete(id: Long) {
        reportDao.deleteById(id)
    }
}
