package com.pratt.superlist

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.qualifiedName

    // [START declare_database_ref]
    private lateinit var database: DatabaseReference
    // [END declare_database_ref]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [START initialize_database_ref]
        database = Firebase.database.reference
        // [END initialize_database_ref]

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val uemail = FirebaseAuth.getInstance().currentUser?.email
        val username = FirebaseAuth.getInstance().currentUser?.displayName


        uname.text = username

        signoutb.setOnClickListener {
            singoutUser()
        }

    }
    private fun singoutUser(){
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut();
        val intent = Intent(this@MainActivity, WelcomeScreen::class.java)
        startActivity(intent)
        finish()
    }



}