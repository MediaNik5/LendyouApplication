package org.medianik.lendyou.ui.home

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.MainActivity
import org.medianik.lendyou.ui.component.DropdownMenuInput
import org.medianik.lendyou.ui.component.EndRow
import org.medianik.lendyou.ui.component.LendyouCard
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.theme.LendyouTheme

@Composable
fun Profile(modifier: Modifier = Modifier) {
    LendyouSurface(modifier = modifier.fillMaxSize()) {
        Box {
            ProfileContent()
        }
    }
}

@Composable
private fun ProfileContent(modifier: Modifier = Modifier) {
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    Column(
        modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(
            photoUrl = currentUser.photoUrl,
            displayName = currentUser.displayName!!
        )
        Settings()
    }
}


@Composable
private fun Settings(modifier: Modifier = Modifier) {
    LendyouSurface(
        modifier
            .padding(LogoCardPadding)
            .fillMaxWidth()
            .heightIn(LogoCardHeight)
            .clip(LogoCardShape)
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            LenderDebtorChoose()
            LanguageChoose()
        }
    }
}

@Composable
fun LanguageChoose() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val locales = listOf("ru", "en")
        val languages = listOf("Russian", "English")
        val context = LocalContext.current as MainActivity
        val selectedIndex = remember {
            mutableStateOf(
                locales.indexOf(context.getSetting("locale", "en"))
            )
        }
        DropdownMenuInput(
            selectedIndex,
            items = languages,
            placeholder = R.string.cancel,
            value = { this },
            onClick = {
                context.setSetting("locale", locales[selectedIndex.value])
                context.recreate()
            }
        )
    }
}

@Composable
private fun LenderDebtorChoose() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.debtor), style = MaterialTheme.typography.h6)
        LenderDebtorSwitch()
        Text(stringResource(R.string.lender), style = MaterialTheme.typography.h6)
    }
    Text(
        text = "App will restart",
        style = MaterialTheme.typography.caption,
        color = LendyouTheme.colors.brandSecondary
    )
}

@Composable
private fun LenderDebtorSwitch() {
    val context = LocalContext.current as MainActivity
    val checked = remember {
        mutableStateOf(
            context.getSetting("debtorLender", "lender") == "lender"
        )
    }
    Switch(
        modifier = Modifier.padding(5.dp),
        checked = checked.value,
        onCheckedChange = { newState ->
            checked.value = newState
            context.setSetting("debtorLender", if (checked.value) "lender" else "debtor")
            context.recreate()
        }
    )
}

private val ImagePadding = 12.dp
private val LogoCardPadding = 5.dp
private val LogoCardHeight = 100.dp
private val LogoCardShape = RoundedCornerShape(16.dp)

@Composable
fun Logo(photoUrl: Uri?, displayName: String, modifier: Modifier = Modifier) {
    val activity: MainActivity = LocalContext.current as MainActivity
    LendyouCard(
        modifier
            .padding(LogoCardPadding)
            .fillMaxWidth()
            .height(LogoCardHeight)
            .clip(LogoCardShape)
    ) {
        Row(
            Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserImage(
                Modifier
                    .padding(vertical = ImagePadding / 2, horizontal = ImagePadding)
                    .size(LogoCardHeight - ImagePadding),
                data = photoUrl ?: R.drawable.placeholder
            )
            Text(
                text = displayName,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.h5
            )
            EndRow(Modifier.padding(end = LogoCardPadding)) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = "Logout",
                    Modifier
                        .clip(CircleShape)
                        .clickable {
                            activity.signOut()
                        }
                )
            }
        }
    }

}

