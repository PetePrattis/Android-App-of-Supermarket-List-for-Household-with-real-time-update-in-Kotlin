package com.pratt.superlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue


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

        uname.text = uid

        signoutb.setOnClickListener {
            singoutUser()
        }

    }
    private fun singoutUser(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@MainActivity, WelcomeScreen::class.java)
        startActivity(intent)
        finish()
    }


}