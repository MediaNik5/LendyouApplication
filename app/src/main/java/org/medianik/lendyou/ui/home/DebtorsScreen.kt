package org.medianik.lendyou.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.ui.MainDestinations
import org.medianik.lendyou.ui.component.LendyouCard
import org.medianik.lendyou.ui.component.LendyouFloatingActionButton
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.component.NothingHereYet
import org.medianik.lendyou.ui.people.NewPerson

fun NavGraphBuilder.addPersonScreenGraph(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(MainDestinations.NEW_PERSON_ROUT) { from ->
        NewPerson(navigateBack)
    }
}

@Composable
fun Debtors(
    modifier: Modifier = Modifier,
    onNewDebtorRequested: () -> Unit,
) {
    val changes = remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes.value++ }

    val debtors = remember(changes.value) {
        Repos.getInstance().getDebtors()
    }
    Repos.getInstance().subscribeToChanges(onChange)
    Box {
        Debtors(
            debtors,
            onChange,
            modifier
        )
        LendyouFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            onClick = onNewDebtorRequested
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "")
        }
    }
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
            val selectedIndex = remember { mutableStateOf(-1) }
            DebtorsList(debtors, onChange, selectedIndex)

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
        if (debtors.isEmpty())
            NothingHereYet(R.string.no_debtors)
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


private val DebtorCardPadding = 6.dp
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
                UserImage(
                    Modifier
                        .size(DebtorCardHeight - DebtorCardPadding)
                        .padding(DebtorCardPadding / 2)
                )
                Text(text = debtor.name, modifier = Modifier.padding(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                val debtsSum = debtor
                    .debts
                    .filter { it.debtInfo.debtorId == debtor.id }
                    .sumOf { it.debtInfo.sum }
                Text(text = "Sum of debts: $debtsSum", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun UserImage(
    modifier: Modifier = Modifier,
    data: Any = R.drawable.placeholder,
    imageDescription: String = "Image"
) {
    LendyouSurface(
        shape = CircleShape,
        elevation = 3.dp,
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(
                data = data,
                builder = {
                    crossfade(true)
                    placeholder(drawableResId = R.drawable.placeholder)
                }
            ),
            modifier = Modifier.fillMaxSize(),
            contentDescription = imageDescription,
            contentScale = ContentScale.Crop
        )
    }
}