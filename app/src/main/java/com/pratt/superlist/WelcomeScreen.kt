package com.pratt.superlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if(user != null) {
                startMainActivity()
            } else {
                startSignInActivity()
            }
        },1500)
    }

    private fun startSignInActivity() {
        val intent = Intent(this@WelcomeScreen, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this@WelcomeScreen, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}