package org.medianik.lendyou.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, this.javaClass)
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//        val client = GoogleSignIn.getClient(this, gso)
//        client.signOut().addOnCompleteListener{ task ->
//            if(task.isSuccessful)
//                Log.i("Lendyou", "Signed out of google")
//            else
//                Log.i("Lendyou", "Could not sign out of google acc, code = ${task.exception}")
//        }

//        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
//        if(lastSignedInAccount == null) {
//            val registerForActivityResult = registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult(),
//                this::onSignedIn
//            )
//            registerForActivityResult.launch(client.signInIntent)
//        }

        setContent {
            LendyouApp()
        }
    }

//    private fun onSignedIn(result: ActivityResult){
//        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//        handleSignInResult(task);
//    }
//
//    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
//        try{
//            task.getResult(ApiException::class.java)
//            Log.w("Lendyou", "Logged in!!!")
//        }catch (e: ApiException){
//            Log.w("Lendyou", "Signin failed, code = ${e.statusCode}")
//        }
//    }
}
