package org.medianik.lendyou.ui

import android.content.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.ui.auth.AuthUser
import org.medianik.lendyou.util.ServerConnection
import org.medianik.lendyou.util.sql.LendyouDatabase

class MainActivity : ComponentActivity() {

    private lateinit var preferences: SharedPreferences
    private lateinit var serverConnection: ServerConnection

    private lateinit var isAuthNeeded: MutableState<Boolean>
    private var firebaseToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getPreferences(MODE_PRIVATE)
        serverConnection = ServerConnection(this)
        initializeBroadcaster()
        Repos.initRepo(LendyouDatabase(this))

        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        setContent {
            HandleAuthParameters()

            if (isAuthNeeded.value) {
                AuthUser(firebaseToken!!, serverConnection)
            } else {
                LendyouApp()
            }
        }
    }

    @Composable
    private fun HandleAuthParameters() {
        isAuthNeeded = remember { mutableStateOf(preferences.getBoolean(isAuthNeededKey, false)) }
        firebaseToken = preferences.getString(firebaseKey, null)

        if (isAuthNeeded.value) {
            val edit = preferences.edit()

            edit.putBoolean(isAuthNeededKey, true)
            edit.putString(firebaseKey, firebaseToken)

            edit.apply()
        } else {
            firebaseToken = null
            val edit = preferences.edit()

            edit.putBoolean(isAuthNeededKey, false)
            edit.putString(firebaseKey, null)

            edit.apply()
        }
    }

    private val firebaseTokenChanged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val edit = preferences.edit()
            firebaseToken = intent!!.getStringExtra(firebaseKey)
            isAuthNeeded.value = true
            edit.putBoolean(isAuthNeededKey, true)
            edit.putString(firebaseKey, firebaseToken)
            edit.apply()
        }
    }

    private val auth = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isAuthNeeded.value = false
            preferences.edit()
                .putBoolean(isAuthNeededKey, false)
                .apply()
        }
    }

    private fun initializeBroadcaster() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(firebaseTokenChanged, IntentFilter(firebaseKey))
        LocalBroadcastManager.getInstance(this).registerReceiver(auth, IntentFilter(successfulAuth))
    }

    companion object {
        const val isAuthNeededKey = "isAuthNeeded"
        const val successfulAuth = "auth"
        const val firebaseKey = "firebaseToken"
    }
}
