package com.imdmp.webrtcsample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

class RTCActivity : AppCompatActivity() {

    interface RTCViewListener {
        fun localFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer)
        fun remoteFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer)

    }

    lateinit var rtcListener: RTCViewListener
    lateinit var localViewRender: SurfaceViewRenderer
    lateinit var remoteViewRenderer: SurfaceViewRenderer


    companion object {
        const val ROLE_KEY = "role_key"
        fun createIntent(context: Context, role: MainActivity.Role): Intent {
            val i = Intent(context, RTCActivity::class.java)
            i.putExtra(ROLE_KEY, role)
            return i
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val role = intent.getSerializableExtra(ROLE_KEY)
        setContent {
            RTCScreen(rtcListener = object : RTCViewListener {
                override fun localFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer) {
                    localViewRender = surfaceViewRenderer
                }

                override fun remoteFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer) {
                    remoteViewRenderer = surfaceViewRenderer
                }

            })
        }
    }
}
