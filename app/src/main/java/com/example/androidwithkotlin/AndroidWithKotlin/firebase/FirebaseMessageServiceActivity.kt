package com.example.androidwithkotlin.AndroidWithKotlin.firebase

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidwithkotlin.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseMessageServiceActivity : AppCompatActivity() {

    private var bannerAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_firebase_message_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bannerAdView = findViewById<AdView>(R.id.adView)

        mobileBannerAds()

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        Log.e("TokenFail", "onComplete: Token is not generated")
                        return
                    }
                    val token = task.getResult()
                    Log.e("Token ", "onComplete: " + token)
                }
            })

        val databaseReference = FirebaseDatabase.getInstance().getReference("CopyWrightK")
        databaseReference.setValue("Mansukh Makwana")


        //multiple data store
        val databaseContact = FirebaseDatabase.getInstance().getReference("ContactsK")
        val contactId = databaseContact.push().getKey()

        val contactsModel: ContactsModel = ContactsModel("Martin Guptil", "8737373737")
        databaseContact.child(contactId!!).setValue(contactsModel)


        //fetch data from firebase
        databaseContact.child(contactId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val model = snapshot.getValue<ContactsModel?>(ContactsModel::class.java)
                Log.e("Name", model?.name!!)
                Log.e("Number", model.number!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DBError", error.toString())
            }
        })
    }

    private fun mobileBannerAds() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        bannerAdView?.loadAd(adRequest)
    }
}