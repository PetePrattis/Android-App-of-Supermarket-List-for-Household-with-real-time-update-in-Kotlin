package com.pratt.superlist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth


class WelcomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser

            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired


            if (user != null) {
                for (user in FirebaseAuth.getInstance().currentUser!!.providerData) {
                    if (user.providerId == "facebook.com") {
                        if (isLoggedIn)
                            startMainActivity()
                        else
                            startSignInActivity()
                    }
                }
                startMainActivity()
            } else {
                startSignInActivity()
            }
        }, 1500)
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