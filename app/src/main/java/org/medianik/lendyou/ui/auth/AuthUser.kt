package org.medianik.lendyou.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.component.getBackgroundColorForElevation
import org.medianik.lendyou.ui.theme.LendyouTheme

@Composable
fun AuthUser() {
    ProvideWindowInsets {
        LendyouTheme {
            LendyouSurface {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        Modifier
                            .padding(40.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                getBackgroundColorForElevation(
                                    color = LendyouTheme.colors.uiBackground,
                                    elevation = 12.dp
                                )
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "You will be asked to authenticate through telegram bot. " +
                                    "It will receive your common user info: username, name, avatar",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(30.dp)
                        )
                        val context = LocalContext.current
                        val intent = remember {
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://t.me/AccessPaymentBot?start=a-a-a-a-a")
                            )
                        }
                        Button(
                            onClick = { context.startActivity(intent) },
                            modifier = Modifier.padding(bottom = 30.dp)
                        ) {
                            Text(
                                "Login",
                                color = LendyouTheme.colors.textInteractive,
                                style = MaterialTheme.typography.button
                            )
                        }
                    }
                }
            }
        }
    }
}