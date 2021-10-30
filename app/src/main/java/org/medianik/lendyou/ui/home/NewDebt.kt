package org.medianik.lendyou.ui.home

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.MainDestinations

fun NavGraphBuilder.addNewDebtGraph(
    modifier: Modifier = Modifier
) {
    composable(MainDestinations.NEW_DEBT_ROUT) { from ->
        Prototype(featureName = R.string.new_debt, modifier = modifier)
//        Cart(onSnackClick = { id -> onDebtSelected(id, from) }, modifier)
    }
}