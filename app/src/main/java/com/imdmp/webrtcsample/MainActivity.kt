package com.imdmp.webrtcsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    enum class Role : Serializable {
        HOST,
        CALLER
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val meetingIdText = remember { mutableStateOf("") }
            // setup simple connection screen
            // ask for meeting id.
            // if meeting id is not used, begin:
            // before sending - make sure to ask for permissions
            // create offer and once successful, send to signalling client.
            // signalling client writes offer in db.


            // joining:
            //

            // setup simple call screen
            // set up signaling client

            LaunchedEffect(key1 = Unit, block = {
                getData()
            })

            Column {
                Text(
                    "Web RTC Smaple",
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextField(value = meetingIdText.value, onValueChange = { meetingIdText.value = it })

                Button({
                    startRtcActivity(Role.HOST)
                }) {
                    Text(text = "Create Meeting")
                }

                Button({
                    startRtcActivity(Role.CALLER)
                }) {
                    Text(text = "Join Meeting")
                }

            }
        }
    }

    private fun startRtcActivity(role: MainActivity.Role) {

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
