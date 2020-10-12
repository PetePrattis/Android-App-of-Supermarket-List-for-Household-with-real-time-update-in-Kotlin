package com.pratt.superlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

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

    }

    private fun singinUser(){
        val email = signinemailtv.text.toString()
        val password = signinpasswordtp.text.toString()

        if (email == "" || password == ""){
            Toast.makeText(this@SignInActivity, "Συμπλήρωσε όλα τα πεδία!",Toast.LENGTH_LONG)
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
                        Toast.makeText(this@SignInActivity, "Email ή Password είναι λάθος!" + task.exception?.message.toString(), Toast.LENGTH_LONG)
                }
        }

    }
    private fun signupUser(){
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}