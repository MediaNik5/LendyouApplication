package org.medianik.lendyou.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.ui.component.LendyouCard
import org.medianik.lendyou.ui.component.LendyouSurface

@Composable
fun Debtors(
    modifier: Modifier = Modifier
) {
    var changes: Int by rememberSaveable { mutableStateOf(0) }
    val onChange: () -> Unit = { changes++ }

    val debtors = rememberSaveable(changes) {
        Repos.getInstance().currentRepo.getDebtors()
    }
    Debtors(
        debtors,
        onChange,
        modifier
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Debtors(
    debtors: List<Debtor>,
    onChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    LendyouSurface(modifier = modifier.fillMaxSize()) {
        Box {
            val selectedIndex = rememberSaveable { mutableStateOf(-1) }
            DebtorsList(debtors, onChange, selectedIndex)
            AnimatedVisibility(visible = selectedIndex.value != -1) {

            }
        }
    }
}

@Composable
fun DebtorsList(
    debtors: List<Debtor>,
    onChange: () -> Unit,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        for (index in debtors.indices) {
            key(debtors[index].id) {
                DebtorItem(
                    debtors[index],
                    index,
                    onChange,
                    selectedIndex
                )
            }
        }
    }
}

private val DebtorCardPadding = 5.dp
private val DebtorCardHeight = 100.dp
private val DebtorCardShape = RoundedCornerShape(16.dp)

@Composable
fun DebtorItem(
    debtor: Debtor,
    index: Int,
    onChange: () -> Unit,
    selectedIndex: MutableState<Int>
) {
    LendyouCard(
        modifier = Modifier
            .padding(DebtorCardPadding)
            .fillMaxWidth()
            .height(DebtorCardHeight)
            .clip(DebtorCardShape)
            .clickable {
                selectedIndex.value = index
            }
    ) {
        Row(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DebtorImage(debtor)
                Text(text = debtor.name, modifier = Modifier.padding(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                val debtsSum = debtor
                    .getDebts()
                    .filter { it.debtInfo.debtorId == debtor.id }
                    .sumOf { it.debtInfo.sum }
                Text(text = "Sum of debts: $debtsSum", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DebtorImage(debtor: Debtor) {
    LendyouSurface(shape = CircleShape, elevation = 3.dp) {
        Image(
            painter = rememberImagePainter(
                data = R.drawable.placeholder,
                builder = {
                    crossfade(true)
                    placeholder(drawableResId = R.drawable.placeholder)
                }
            ),
            contentDescription = "Content"
        )
    }
}
