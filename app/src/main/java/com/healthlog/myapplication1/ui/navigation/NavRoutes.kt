package com.healthlog.myapplication1.ui.navigation

object NavRoutes {
    const val SPLASH     = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME       = "home"
    const val RECORD     = "record"
    const val CALENDAR   = "calendar"
    const val GRAPH      = "graph"
    const val SETTINGS   = "settings"
}

enum class BottomTab(val route: String, val label: String, val icon: String) {
    HOME    (NavRoutes.HOME,     "홈",     "home"),
    RECORD  (NavRoutes.RECORD,   "기록",   "history"),
    CALENDAR(NavRoutes.CALENDAR, "캘린더", "calendar_month"),
    GRAPH   (NavRoutes.GRAPH,    "그래프", "show_chart"),
    SETTINGS(NavRoutes.SETTINGS, "설정",  "settings")
}
