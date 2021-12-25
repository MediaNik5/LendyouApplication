package org.medianik.lendyou.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.util.ServerDatabase
import org.medianik.lendyou.util.sql.LendyouDatabase

class MainActivity : ComponentActivity() {

    private lateinit var googleSingInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var serverDatabase: ServerDatabase
    private lateinit var lendyouDatabase: LendyouDatabase

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LendyouApplication)
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        signIn()
    }


    fun getSetting(id: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(id, defaultValue)
    }

    fun setSetting(id: String, value: String) {
        sharedPreferences.edit().putString(id, value).apply()
    }

    private fun updateUi() {
        sharedPreferences = getSharedPreferences("Lendyou", MODE_PRIVATE)
        serverDatabase = ServerDatabase(
            FirebaseDatabase.getInstance(getString(R.string.server_database)),
            firebaseAuth.currentUser!!.uid
        )
        lendyouDatabase = LendyouDatabase(this)
        Repos.initRepo(
            lendyouDatabase,
            serverDatabase,
            firebaseAuth,
            sharedPreferences.getString("debtorLender", "lender") == "lender"
        )
        setContent {
            LendyouApp()
        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_firebase_client_id))
            .build()
        googleSingInClient = GoogleSignIn.getClient(this, gso)

        val acc: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (acc == null) {
            val signInIntent = googleSingInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            firebaseAuthWithGoogle(acc.idToken!!)
            updateUi()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Lendyou", "firebaseAuthWithGoogle: ${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("Lendyou", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Lendyou", "Successful sign in!")
                    val user = firebaseAuth.currentUser
                    if (user != null)
                        updateUi()
                } else {
                    Log.w("Lendyou", "singInWithCredential:failure", task.exception)
                }
            }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        lendyouDatabase.close()
        deleteDatabase(LendyouDatabase.DATABASE_NAME)
        googleSingInClient.signOut().addOnSuccessListener { it: Void? ->
            finishAffinity()
        }.addOnFailureListener { it: Exception ->
            Log.e("Lendyou", "Exception happened while signing out of account", it)
        }
    }

    override fun toString(): String {
        return "MainActivity()"
    }


    companion object {
        private const val RC_SIGN_IN = 100
        const val isAuthNeededKey = "isAuthNeeded"
        const val successfulAuth = "auth"
        const val firebaseKey = "firebaseToken"
    }
}
