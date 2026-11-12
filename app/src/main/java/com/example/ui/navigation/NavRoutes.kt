package com.example.ui.navigation

sealed class Screen(val route: String, val titleKey: String) {
    data object Home : Screen("home", "nav_home")
    data object Scan : Screen("scan", "nav_scan")
    data object MyFarm : Screen("my_farm", "nav_my_farm")
    data object History : Screen("history", "nav_history")
    data object Profile : Screen("profile", "nav_profile")
    data object ScanResult : Screen("scan_result", "ai_estimated_result")
    data object AIChat : Screen("ai_chat", "ai_chat")
    data object Weather : Screen("weather", "weather_summary")
    data object KnowledgeCenter : Screen("knowledge_center", "knowledge_center")
    data object CropCalendar : Screen("crop_calendar", "crop_calendar")
    data object ExpertHelp : Screen("expert_help", "expert_help")
    data object Onboarding : Screen("onboarding", "app_title")
    data object Auth : Screen("auth", "login_prompt")
    data object AboutDeveloper : Screen("about_developer", "about_developer")
}
