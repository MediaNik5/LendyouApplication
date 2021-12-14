package org.medianik.lendyou.ui.home

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.person.Person
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
    onNewPersonRequested: () -> Unit,
) {
    val changes = remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes.value++ }

    val debtors = remember(changes.value) {
        Repos.getInstance().getDebtors()
    }
    Repos.getInstance().subscribeToChanges(onChange)
    Box(Modifier.fillMaxSize()) {
        if (debtors.isEmpty()) {
            NothingHereYet(R.string.no_debtors)
        } else {
            Persons(
                persons = debtors,
                onChange = onChange,
                modifier = modifier,
            ) { person ->
                val debtsSum = Repos.getInstance().getDebts()
                    .filter { debt -> debt.debtInfo.debtorId == person.id }.sumOf { it.left }
                Text(
                    stringResource(R.string.sum_of_debts).replace("%s", debtsSum.toString()),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        LendyouFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            onClick = onNewPersonRequested
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "")
        }
    }
}

@Composable
fun <T : Person> Persons(
    persons: List<T>,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
    description: @Composable RowScope.(T) -> Unit
) {
    LendyouSurface(modifier = modifier.fillMaxSize()) {
        Box {
            val selectedIndex = remember { mutableStateOf(-1) }
            PersonsList(persons, onChange, selectedIndex, description)
        }
    }
}

@Composable
fun <T : Person> PersonsList(
    persons: List<T>,
    onChange: () -> Unit,
    selectedIndex: MutableState<Int>,
    description: @Composable RowScope.(T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        for (index in persons.indices) {
            key(persons[index].id) {
                PersonItem(
                    persons[index],
                    index,
                    onChange,
                    selectedIndex,
                    description,
                )
            }
        }
    }
}


private val DebtorCardPadding = 6.dp
private val DebtorCardHeight = 100.dp
private val DebtorCardShape = RoundedCornerShape(16.dp)

@Composable
fun <T : Person> PersonItem(
    person: T,
    index: Int,
    onChange: () -> Unit,
    selectedIndex: MutableState<Int>,
    description: @Composable RowScope.(T) -> Unit
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
                Text(text = person.name, modifier = Modifier.padding(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                content = { description(person) }
            )
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