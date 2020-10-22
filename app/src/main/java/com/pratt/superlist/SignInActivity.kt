package com.pratt.superlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.*


class SignInActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private val RC_SIGN_IN = 9001

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    val loginManager: LoginManager = LoginManager.getInstance()
    private lateinit var callbackManager: CallbackManager
    private lateinit var refUsers: DatabaseReference

    lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        signinb.setOnClickListener{
            singinUser()
        }

        signupb.setOnClickListener{
            signupUser()
        }

        callbackManager = CallbackManager.Factory.create()

        //fbsigninb.setReadPermissions(Arrays.asList("email"))
        fbsigninb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.e("!!!!!!!!", "INSIDE onSuccess")
                handleFacebookAccessToken(result.accessToken)
                //val intent = Intent(this@SignInActivity, WelcomeScreen::class.java)
                //startActivity(intent)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })

        configureGoogleClient()

        googlesignin.setOnClickListener{
            googleSignIn()
        }
        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun singinUser(){
        val email = signinemailtv.text.toString()
        val password = signinpasswordtp.text.toString()

        if (email == "" || password == ""){
            Toast.makeText(this@SignInActivity, "Συμπλήρωσε όλα τα πεδία!", Toast.LENGTH_LONG)
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                        Toast.makeText(
                            this@SignInActivity,
                            "Email ή Password είναι λάθος!" + task.exception?.message.toString(),
                            Toast.LENGTH_LONG
                        )
                }
        }

    }
    private fun signupUser(){
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun configureGoogleClient(){
        val options : GoogleSignInOptions? = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API,options!!)
            .build()
        mGoogleApiClient.connect()
    }

    private fun googleSignIn(){
        val signInIntent :Intent? = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        startActivityForResult(signInIntent , RC_SIGN_IN)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
//                // ...
//            }
//        }
//    }

    private fun handleFacebookAccessToken(token: AccessToken){
        Log.e("!!!!!!!!", "INSIDE handleFacebookAccessToken")
        val credential = FacebookAuthProvider.getCredential(token.token)

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Log.e("!!!!!!!!", "INSIDE addOnCompleteListener")
                    val isNew = task.result!!.additionalUserInfo!!.isNewUser
                    if (isNew)
                        registernewfbUser()
                    else{

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }

                }
                else
                    Toast.makeText(
                        this@SignInActivity,
                        "Λάθος credentials!" + task.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    )
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("!!!!!!!!", "Inside onActivityResult")
        //todo hide everything from screen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("SignInActivity", "Google sign in failed", e)
                // ...
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    //todo
                    //-------------------------------------------------//
                    //υλοποίησε μια αντίστοιχη function με την registernewfbUser() αντί για τον παρακάτω κώδικα ονόματι registernewgmailUser()
                    //οι δύο αυτές functions θα έχουν ίδια λειτουργία και θα πρέπει να ελέγξεις οτι λειτουργεί η εισαγωγή των πληροφοριών του νέου χρήστη στην βάση
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    //------------------------------------------------//
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    // ...

                }

                // ...
            }
    }

    private fun registernewfbUser(){
        Log.e("!!!!!!!!", "INSIDE registernewfbUser")
        val firebaseUserID = mAuth.currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseUserID)

        val userHashMap = HashMap<String, Any>()
        val username = FirebaseAuth.getInstance().currentUser?.displayName
        userHashMap["uname"] = username.toString()


        refUsers.updateChildren(userHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this@SignInActivity,
                        "Error Message:" + task.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(
            this@SignInActivity,
            "Error Message:" + p0.errorMessage.toString(),
            Toast.LENGTH_LONG
        ).show()
    }
}