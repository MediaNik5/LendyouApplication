package org.medianik.lendyou.ui

//import org.medianik.lendyou.ui.auth.AuthUser
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.messaging.FirebaseMessaging
import io.vertx.core.Vertx
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.auth.ClientVertx

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vertx = Vertx.vertx()
        vertx.deployVerticle(ClientVertx())

        firebaseToken()
        setContent {
            if (false) {
//                AuthUser()
            } else {
                LendyouApp()
            }
        }
    }

    private fun firebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("Lendyou", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("Lendyou", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

}
