package com.pratt.superlist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_dialognewhousehold.view.*
import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.qualifiedName
    // [START declare_database_ref]
    private lateinit var database: DatabaseReference
    // [END declare_database_ref]

    lateinit var mRecyclerView: RecyclerView

    private var householdkeyList: ArrayList<String> = arrayListOf()
    private var householdnameList: ArrayList<String> = arrayListOf()

    private var firebaseQueries: FirebaseQueries = FirebaseQueries()

    private var hList: List<HouseholdsModel> = ArrayList()
    private var hListAdapter: HouseholdsListAdapter = HouseholdsListAdapter(hList)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [START initialize_database_ref]
        database = Firebase.database.reference
        // [END initialize_database_ref]

        loadHouseholdInfo()




        newhouseholdb.setOnClickListener {
            openDialog()
        }

        signoutb.setOnClickListener {
            singoutUser()
        }

    }

    private fun loadHouseholdInfo(){
        val uid = firebaseQueries.getUser()?.uid
        val uemail = firebaseQueries.getUser()?.email
        val username = firebaseQueries.getUser()?.displayName

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                householdkeyList.clear()
                householdnameList.clear()
                for (child: DataSnapshot in snapshot.child("users").child(uid.toString())
                    .child("households").children) {
                    val hkey = child.key.toString()
                    householdkeyList.add(hkey)
                    Log.e("Household ", hkey)
                }
                for (key in householdkeyList) {
                    for (child: DataSnapshot in snapshot.child("households").children) {
                        if (key == child.key.toString()) {
                            val hname = child.child("h_name").value.toString()
                            householdnameList.add(hname)
                            Log.e("Household ", hname)
                            break
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }

    private fun singoutUser(){
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut();
        
        val intent = Intent(this@MainActivity, WelcomeScreen::class.java)
        startActivity(intent)
        finish()
    }


    fun openDialog(){

        val dialog = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.layout_dialognewhousehold)

        dialog.findViewById<Button>(R.id.dialogcreatehousholdb).setOnClickListener {
            Log.e("Here","onclick create")
            //check if inputs exist
            //dismiss dialog
            dialog.dismiss()
            //get household info
            //val householdname = dialogNewHouseholdView.hnameet.text.toString()
            //val householdpassword = dialogNewHouseholdView.hpasswordet.text.toString()
            //create household child in firebase realtime db

        }
        dialog.findViewById<Button>(R.id.dialogcancelhouseholdb).setOnClickListener {
            Log.e("Here","onclick cancel")
            dialog.dismiss()
        }

        dialog.show()

    }

}