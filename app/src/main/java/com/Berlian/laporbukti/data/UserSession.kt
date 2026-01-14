package com.Berlian.laporbukti.data

object UserSession {
    var role: String = "USER"

    fun isAdmin(): Boolean = role == "ADMIN"
}
