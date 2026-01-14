package com.Berlian.laporbukti.navigation

sealed class Routes(val route: String) {

    data object Login : Routes("login")
    data object Home : Routes("home")
    data object Statistics : Routes("statistics")
    data object Create : Routes("create")
    data object History : Routes("history")
    data object Detail : Routes("detail/{id}") {
        fun build(id: Long) = "detail/$id"
    }
}
