package org.medianik.lendyou.ui.auth

//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.android.volley.VolleyError
//import com.google.accompanist.insets.ProvideWindowInsets
//import org.medianik.lendyou.R
//import org.medianik.lendyou.ui.component.LendyouSurface
//import org.medianik.lendyou.ui.component.getBackgroundColorForElevation
//import org.medianik.lendyou.ui.theme.LendyouTheme
//import org.medianik.lendyou.util.ServerConnection
//import java.util.*
//import java.util.concurrent.atomic.AtomicBoolean
//
//@Composable
//fun AuthUser(firebaseToken: String, serverConnection: ServerConnection) {
//    ProvideWindowInsets {
//        LendyouTheme {
//            LendyouSurface {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    LoginItem(firebaseToken, serverConnection)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun LoginItem(
//    firebaseToken: String,
//    serverConnection: ServerConnection
//) {
//    Column(
//        Modifier
//            .padding(40.dp)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(20.dp))
//            .background(
//                getBackgroundColorForElevation(
//                    color = LendyouTheme.colors.uiBackground,
//                    elevation = 12.dp
//                )
//            ),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        LoginContents(firebaseToken, serverConnection)
//    }
//}
//
//@Composable
//private fun LoginContents(
//    firebaseToken: String,
//    serverConnection: ServerConnection
//) {
//    PromptInfo()
//    LoginButton(firebaseToken, serverConnection)
//}
//
//@Composable
//private fun PromptInfo() {
//    Text(
//        stringResource(R.string.telegram_login),
//        textAlign = TextAlign.Center,
//        modifier = Modifier.padding(30.dp)
//    )
//}
//
//@Composable
//private fun LoginButton(
//    firebaseToken: String,
//    serverConnection: ServerConnection
//) {
//    val onClick: () -> Unit = handleLoginButton(firebaseToken, serverConnection)
//    var clicked = false
//    Button(
//        onClick = {
//            if (!clicked) {
//                onClick()
//                clicked = true
//            }
//        },
//        modifier = Modifier.padding(bottom = 30.dp),
//        content = { LoginButtonText() }
//    )
//}
//
//@Composable
//private fun LoginButtonText() {
//    Text(
//        stringResource(R.string.telegram_login_button),
//        color = LendyouTheme.colors.textInteractive,
//        style = MaterialTheme.typography.button
//    )
//}
//
//@Composable
//private fun handleLoginButton(
//    firebaseToken: String,
//    serverConnection: ServerConnection
//): () -> Unit {
//    val context = LocalContext.current
//    var uuid: UUID? = null
//    // used to indicate whether one of the following events happened:
//    // server response, user button press
//    // it is set to true if any of those has happened. Used as atomic to support happens-before
//    val flag = AtomicBoolean(false)
//    serverConnection.request(
//        firebaseToken,
//        { response ->
//            Log.d("Lendyou", "Response was: $response")
//
//            if (response.result == "none") {
//                onBadResponse(context)
//            } else {
//                try {
//                    uuid = UUID.fromString(response.result)
//                    if (shouldRedirectToTelegram(flag)) {
//                        context.startActivity(redirectToTelegram(uuid))
//                    }
//                } catch (e: IllegalArgumentException) {
//                    onBadResponse(context)
//                } catch (e: NumberFormatException) {
//                    onBadResponse(context)
//                }
//            }
//        },
//        { onBadServer(it, context) }
//    )
//
//    return {
//        if (shouldRedirectToTelegram(flag))
//            context.startActivity(redirectToTelegram(uuid))
//    }
//}
//
//private fun onBadResponse(context: Context) {
//    Log.e("Lendyou", "Server could not accept firebase token.")
//    Toast.makeText(context, R.string.bad_response, Toast.LENGTH_LONG).show()
//}
//
//private fun shouldRedirectToTelegram(flag: AtomicBoolean) =
//    !flag.compareAndSet(false, true)
//
//private fun redirectToTelegram(uuid: UUID?) =
//    Intent(
//        Intent.ACTION_VIEW,
//        Uri.parse("https://t.me/AccessPaymentBot?start=${uuid!!}")
//    )
//
//private fun onBadServer(e: VolleyError, context: Context) {
//    Log.e("Lendyou", "Server doesn't respond.")
//    Toast.makeText(context, R.string.bad_server, Toast.LENGTH_LONG).show()
//}