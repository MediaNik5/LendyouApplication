package org.medianik.lendyou.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import org.medianik.lendyou.ui.component.LendyouScaffold
import org.medianik.lendyou.ui.component.LendyouSnackbar
import org.medianik.lendyou.ui.home.*
import org.medianik.lendyou.ui.theme.LendyouTheme


@Composable
fun LendyouApp() {
    val context = LocalContext.current as MainActivity
    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
        LendyouTheme {
            val appState = rememberLendyouAppState()
            LendyouScaffold(
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        LendyouBottomBar(
                            tabs = appState.bottomBarTabs.filter {
                                filterForDebtorLender(
                                    it,
                                    context
                                )
                            },
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
                    lendyouNavGraph(
                        onDebtSelected = appState::navigateToDebtDetail,
                        onNewDebtRequested = appState::navigateToNewDebt,
                        onPendingDebtsRequested = appState::navigateToPendingDebts,
                        onNewPersonRequested = appState::navigateToNewPerson,
                        navigateBack = appState::navigateBack
                    )
                }
            }
        }
    }
}

fun filterForDebtorLender(section: HomeSections, context: MainActivity): Boolean {
    return if (context.getSetting("debtorLender") == "lender")
        section != HomeSections.LENDERS
    else section != HomeSections.DEBTORS
}


private fun NavGraphBuilder.lendyouNavGraph(
    onDebtSelected: (Long, NavBackStackEntry) -> Unit,
    onNewDebtRequested: (NavBackStackEntry) -> Unit,
    onNewPersonRequested: (NavBackStackEntry) -> Unit,
    onPendingDebtsRequested: (NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.DEBTS.route
    ) {
        addHomeGraph(
            onDebtSelected,
            onNewDebtRequested,
            onNewPersonRequested,
            onPendingDebtsRequested
        )
    }
    addPersonScreenGraph(navigateBack)
    addDebtScreenGraph(onPendingDebtsRequested, navigateBack)
}
