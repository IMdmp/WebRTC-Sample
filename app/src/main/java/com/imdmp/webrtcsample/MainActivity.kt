package com.imdmp.webrtcsample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.io.Serializable

private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
private const val CAMERA_AUDIO_PERMISSION_REQUEST_CODE = 1

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
//                getData()
                requestPermissionsForDevice()
            })

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "Web RTC Smaple",
                    modifier = Modifier.fillMaxWidth(),
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

    private fun requestPermissionsForDevice() {
        if ((ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this,AUDIO_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED)) {
            requestCameraAndAudioPermission()
        }
    }

    private fun requestCameraAndAudioPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, AUDIO_PERMISSION) &&
            !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION), CAMERA_AUDIO_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera And Audio Permission Required")
            .setMessage("This app need the camera and audio to function")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                requestCameraAndAudioPermission(true)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                onCameraPermissionDenied()
            }
            .show()
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this, "Camera and Audio is required for this app.", Toast.LENGTH_LONG).show()
        requestCameraAndAudioPermission()
    }

    private fun startRtcActivity(role: Role) {
        val i = RTCActivity.createIntent(this, role)
        startActivity(i)
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
