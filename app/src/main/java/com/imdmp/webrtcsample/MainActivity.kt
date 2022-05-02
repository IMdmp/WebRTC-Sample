package com.imdmp.webrtcsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // setup simple connection screen
            // setup simple call screen
            // set up signaling client

            LaunchedEffect(key1 = Unit, block = {
                getData()
            })
        }
    }

    fun getData() {
        val db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Timber.d("success")
                for (document in result) {
                    Timber.d("${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Error getting documents.", exception)
            }
    }
}