package com.jeklov.dns.ui


import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeklov.dns.MainActivity
import com.jeklov.dns.ui.composables.menu.BottomBarUI
import com.jeklov.dns.ui.screens.Screens
import com.jeklov.dns.ui.screens.cart.CartPageUI
import com.jeklov.dns.ui.screens.catalog.CatalogPageUI
import com.jeklov.dns.ui.screens.configurator.ConfiguratorPageUI
import com.jeklov.dns.ui.screens.main.MainPageUI
import com.jeklov.dns.ui.screens.profile.ProfilePageUI
import com.jeklov.dns.ui.screens.shorts.ShortsPageUI

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavMenuUI(
    //database: MainDB,
    context: MainActivity,
    application: Application,
) {

    val screenItemsBar = listOf(
        // Main
        Screens.MainPage,

        // Shorts
        Screens.ShortsPage,

        // Configurator
        Screens.ConfiguratorPage,

        // Catalog
        Screens.CatalogPage,

        // Cart
        Screens.CartPage,

        // Profile
        Screens.ProfilePage,
    )

    val navigationController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    // State of topBar, set state to false, if current page showTopBar = false
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    // State of bottomBar, set state to false, if current page showBottomBar = false
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }



    // Some logic for hide bottom menu in some pages (Screens.SomePage.showBottomBar)
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    screenItemsBar.forEach { screens ->

        // Find real address without transmitting data
        val route = navBackStackEntry?.destination?.route
        val index = route?.indexOf("/")

        val realRoute =
            if (index == -1) route else navBackStackEntry?.destination?.route?.substring(
                0, index!!
            )
        if (realRoute == screens.screen) {
            bottomBarState.value = screens.showBottomBar
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            BottomBarUI(navigationController, screenItemsBar, bottomBarState)
        },
        containerColor = Color.White //AppTheme.colorScheme.background
    ) { paddingValues -> // padding of top bar and bottom bar
        NavHost(
            navController = navigationController, startDestination = Screens.MainPage.screen
        ) {
            // Main page
            composable(Screens.MainPage.screen) {
                MainPageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }

            // Shorts page
            composable(Screens.ShortsPage.screen) {
                ShortsPageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }

            // Configurator page
            composable(Screens.ConfiguratorPage.screen) {
                ConfiguratorPageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }

            // Catalog page
            composable(Screens.CatalogPage.screen) {
                CatalogPageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }

            // Cart page
            composable(Screens.CartPage.screen) {
                CartPageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }

            // Profile page
            composable(Screens.ProfilePage.screen) {
                ProfilePageUI(
                    paddingValues = paddingValues,
                    navigationController = navigationController
                )
            }
        }
    }
}