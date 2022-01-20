package org.medianik.lendyou.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.ui.component.LendyouFloatingActionButton
import org.medianik.lendyou.ui.component.NothingHereYet

@Composable
fun Lenders(
    modifier: Modifier = Modifier,
    onNewPersonRequested: () -> Unit
) {

    val changes = remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes.value++ }

    val lenders = remember(changes.value) {
        Repos.getInstance().getLenders()
    }
    Repos.getInstance().subscribeToChanges(onChange)
    Box(Modifier.fillMaxSize()) {
        if (lenders.isEmpty()) {
            NothingHereYet(R.string.no_lenders)
        } else {
            Persons(
                persons = lenders,
                onChange = onChange,
                modifier = modifier
            ) { person ->
                val debtsSum = Repos.getInstance().getDebts()
                    .filter { debt -> debt.debtInfo.lenderId == person.id }.sumOf { it.left }
                Text(
                    stringResource(R.string.sum_of_debts).replace("%s", debtsSum.toString()),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        LendyouFloatingActionButton(onClick = onNewPersonRequested)
    }
}




















