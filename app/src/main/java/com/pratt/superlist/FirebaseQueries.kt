package com.pratt.superlist

import bolts.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FirebaseQueries {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun registerUserInfo(){
        //todo(upload user information(uid, uname) to realtime firebase)
    }

//    fun getHouseholdsList(): Task<Que>{
//        return database.
//    }
}