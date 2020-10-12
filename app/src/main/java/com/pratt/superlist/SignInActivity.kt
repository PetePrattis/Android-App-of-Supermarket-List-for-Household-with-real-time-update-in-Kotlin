package com.pratt.superlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.*


class SignInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    //val loginManager: LoginManager = LoginManager.getInstance()
    private lateinit var callbackManager: CallbackManager
    private lateinit var refUsers: DatabaseReference

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
        fbsigninb.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                Log.e("!!!!!!!!", "INSIDE onSuccess")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })


//        fbsigninb.setOnClickListener{
//            Log.e("!!!!!!!!", "INSIDE bloginb.setOnClickListener")
//            fbsigninUser()
//        }

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

    /*private fun fbsigninUser(){
        Log.e("!!!!!!!!", "INSIDE fbsigninUser")
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.e("!!!!!!!!", "INSIDE onSuccess")
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException?) {
                    TODO("Not yet implemented")
                }

            })
    }*/

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
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


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
}