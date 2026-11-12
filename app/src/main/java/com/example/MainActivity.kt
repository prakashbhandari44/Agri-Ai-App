package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.model.AnalysisResult
import com.example.data.service.localized
import com.example.ui.navigation.Screen
import com.example.ui.screens.AboutDeveloperScreen
import com.example.ui.screens.AIChatScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CropCalendarScreen
import com.example.ui.screens.ExpertConsultationScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KnowledgeScreen
import com.example.ui.screens.MyFarmScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScanResultScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KrishiAiApp(viewModel = viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val titleKey: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun KrishiAiApp(viewModel: FarmViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var activeResult by remember { mutableStateOf<AnalysisResult?>(null) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "nav_home", Icons.Default.Home, "bottom_nav_home"),
        BottomNavItem(Screen.Scan, "nav_scan", Icons.Default.CameraAlt, "bottom_nav_scan"),
        BottomNavItem(Screen.MyFarm, "nav_my_farm", Icons.Default.Grass, "bottom_nav_my_farm"),
        BottomNavItem(Screen.History, "nav_history", Icons.Default.History, "bottom_nav_history"),
        BottomNavItem(Screen.Profile, "nav_profile", Icons.Default.Person, "bottom_nav_profile")
    )

    val isBottomBarVisible = currentScreen in listOf(
        Screen.Home,
        Screen.Scan,
        Screen.MyFarm,
        Screen.History,
        Screen.Profile
    )

    // BackHandler for secondary screens
    BackHandler(enabled = !isBottomBarVisible || currentScreen != Screen.Home) {
        if (!isBottomBarVisible) {
            currentScreen = previousScreen
        } else if (currentScreen != Screen.Home) {
            currentScreen = Screen.Home
        }
    }

    fun navigateTo(screen: Screen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = item.screen },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = localized(item.titleKey)
                                )
                            },
                            label = {
                                Text(
                                    text = localized(item.titleKey),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = KrishiGreenPrimary,
                                selectedTextColor = KrishiGreenPrimary,
                                indicatorColor = KrishiGreenContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToScan = { currentScreen = Screen.Scan },
                    onNavigateToMyFarm = { currentScreen = Screen.MyFarm },
                    onNavigateToWeather = { navigateTo(Screen.Weather) },
                    onNavigateToCropCalendar = { navigateTo(Screen.CropCalendar) },
                    onNavigateToExpertHelp = { navigateTo(Screen.ExpertHelp) },
                    onNavigateToAIChat = { navigateTo(Screen.AIChat) },
                    onNavigateToKnowledge = { navigateTo(Screen.KnowledgeCenter) },
                    onViewScanResult = { result ->
                        activeResult = result
                        navigateTo(Screen.ScanResult)
                    },
                    onNavigateToAboutDeveloper = { navigateTo(Screen.AboutDeveloper) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.Scan -> {
                ScanScreen(
                    viewModel = viewModel,
                    onAnalysisComplete = { result ->
                        activeResult = result
                        navigateTo(Screen.ScanResult)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.MyFarm -> {
                MyFarmScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.History -> {
                HistoryScreen(
                    viewModel = viewModel,
                    onViewScanResult = { result ->
                        activeResult = result
                        navigateTo(Screen.ScanResult)
                    },
                    onNavigateToScan = { currentScreen = Screen.Scan },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.Profile -> {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAuth = { navigateTo(Screen.Auth) },
                    onNavigateToAboutDeveloper = { navigateTo(Screen.AboutDeveloper) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.ScanResult -> {
                activeResult?.let { result ->
                    ScanResultScreen(
                        result = result,
                        viewModel = viewModel,
                        onBack = { currentScreen = previousScreen },
                        onConsultExpert = { navigateTo(Screen.ExpertHelp) },
                        onScanAnother = { currentScreen = Screen.Scan },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            Screen.AIChat -> {
                AIChatScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.Weather -> {
                WeatherScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.KnowledgeCenter -> {
                KnowledgeScreen(
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.CropCalendar -> {
                CropCalendarScreen(
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.ExpertHelp -> {
                ExpertConsultationScreen(
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.Onboarding -> {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinishOnboarding = { currentScreen = Screen.Home }
                )
            }
            Screen.Auth -> {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = { currentScreen = Screen.Home }
                )
            }
            Screen.AboutDeveloper -> {
                AboutDeveloperScreen(
                    onBack = { currentScreen = previousScreen },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
