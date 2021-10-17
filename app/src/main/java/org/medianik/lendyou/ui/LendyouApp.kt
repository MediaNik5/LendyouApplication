package org.medianik.lendyou.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import org.medianik.lendyou.ui.component.LendyouScaffold
import org.medianik.lendyou.ui.component.LendyouSnackbar
import org.medianik.lendyou.ui.home.HomeSections
import org.medianik.lendyou.ui.home.LendyouBottomBar
import org.medianik.lendyou.ui.home.addHomeGraph
import org.medianik.lendyou.ui.theme.LendyouTheme


@Composable
fun LendyouApp() {
    ProvideWindowInsets {
        LendyouTheme {
            val appState = rememberLendyouAppState()
            LendyouScaffold(
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        LendyouBottomBar(
                            tabs = appState.bottomBarTabs,
                            currentRoute = appState.currentRoute!!,
                            navigateToRoute = appState::navigateToBottomBarRoute/*{ }*/
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.systemBarsPadding(),
                        snackbar = { snackbarData -> LendyouSnackbar(snackbarData) }
                    )
                },
                scaffoldState = appState.scaffoldState
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = MainDestinations.HOME_ROUTE,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    jetsnackNavGraph(
                        onSnackSelected = appState::navigateToSnackDetail,
                        upPress = appState::upPress
                    )
                }
            }
        }
    }
}

private fun NavGraphBuilder.jetsnackNavGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    upPress: () -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.DEBTS.route
    ) {
        addHomeGraph(onSnackSelected)
    }
    composable(
        "${MainDestinations.DEBT_DETAIL_ROUTE}/{${MainDestinations.DEBT_ID_KEY}}",
        arguments = listOf(navArgument(MainDestinations.DEBT_ID_KEY) { type = NavType.LongType })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val snackId = arguments.getLong(MainDestinations.DEBT_ID_KEY)
        Box(Modifier.fillMaxSize()) {}
//        SnackDetail(snackId, upPress)
    }
}
