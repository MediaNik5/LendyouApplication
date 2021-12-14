package org.medianik.lendyou.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.theme.LendyouTheme


@Composable
fun NothingHereYet(@StringRes placeholder: Int, modifier: Modifier = Modifier) {
    LendyouSurface(
        contentColor = LendyouTheme.colors.textPrimary
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .height(600.dp)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.not_found),
                contentDescription = null
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(placeholder),
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}