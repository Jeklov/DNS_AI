package com.jeklov.dns.ui.screens

import com.jeklov.dns.R

sealed class Screens(
    val titleRes: Int,
    val screen: String,
    val lottieRes: Int? = null,
    val showIconOnBottomBar: Boolean = true,
    val showBottomBar: Boolean = true,
) {
    // Main page
    data object MainPage : Screens(
        titleRes = R.string.main_page,
        screen = "main_page",
        lottieRes = R.raw.home_nav_button_animation,
    )

    // Search page
    data object SearchPage : Screens(
        titleRes = R.string.search_page,
        screen = "search_page",
        showIconOnBottomBar = false,
        showBottomBar = false,
    )

    //Product

    // Product page
    data object ProductPage : Screens(
        titleRes = R.string.product_page,
        screen = "product_page",
        showIconOnBottomBar = false,
        showBottomBar = false,
    )

    // Product list page
    data object ProductListPage : Screens(
        titleRes = R.string.product_list_page,
        screen = "product_list_page",
        showIconOnBottomBar = false,
        showBottomBar = false,
    )

    // Assistant

    // Assistant page
    data object AssistantPage : Screens(
        titleRes = R.string.assistant_page,
        screen = "assistant_page",
        showIconOnBottomBar = false,
        showBottomBar = false,
    )

    // Assistant history page
    data object AssistantHistoryPage : Screens(
        titleRes = R.string.assistant_history_page,
        screen = "assistant_history_page",
        showIconOnBottomBar = false,
        showBottomBar = false,
    )

    // Shorts
    data object ShortsPage : Screens(
        titleRes = R.string.shorts_page,
        screen = "shorts_page",
        lottieRes = R.raw.shorts_nav_button_animation,
    )

    // Configurator
    data object ConfiguratorPage : Screens(
        titleRes = R.string.configurator_page,
        screen = "configurator_page",
        lottieRes = R.raw.rsu_nav_button_animation,
    )

    // Catalog
    data object CatalogPage : Screens(
        titleRes = R.string.catalog_page,
        screen = "catalog_page",
        lottieRes = R.raw.catalog_nav_button_animation,
    )

    // Cart
    data object CartPage : Screens(
        titleRes = R.string.cart_page,
        screen = "cart_page",
        lottieRes = R.raw.cart_nav_button_animation,
    )

    // Profile
    data object ProfilePage : Screens(
        titleRes = R.string.profile_page,
        screen = "profile_page",
        lottieRes = R.raw.profile_nav_button_animation,
    )
}