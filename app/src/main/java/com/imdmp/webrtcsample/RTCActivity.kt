package com.imdmp.webrtcsample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import timber.log.Timber

val stunServers =
    listOf(
        IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )

class RTCActivity : AppCompatActivity(), PeerConnectionCallbacks,SignalingClientListener {

    interface RTCViewListener {
        fun localFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer)
        fun remoteFinishedRendering(surfaceViewRenderer: SurfaceViewRenderer)

    }

    lateinit var rtcListener: RTCViewListener
    lateinit var localViewRender: SurfaceViewRenderer
    lateinit var remoteViewRenderer: SurfaceViewRenderer
    var audioTrack: AudioTrack? = null
    lateinit var peerConnectionBridge: PeerConnectionBridge

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private val signalingClient = RTCModule.provideSignalingClient(this)
    private lateinit var meetingId: String

    companion object {
        const val ROLE_KEY = "role_key"
        const val MEETING_ID_KEY = "meetingId"
        fun createIntent(context: Context, role: MainActivity.Role, meetingId: String): Intent {
            val i = Intent(context, RTCActivity::class.java)
            i.putExtra(ROLE_KEY, role)
            i.putExtra(MEETING_ID_KEY, meetingId)
            return i
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val role = intent.getSerializableExtra(ROLE_KEY) as MainActivity.Role
        meetingId = intent.getStringExtra(MEETING_ID_KEY)!!
        peerConnectionFactory = RTCModule.buildPeerConnectionFactory(application)
        setup()
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

        peerConnectionBridge.createPeerConnection(
            iceServers = stunServers,
            meetingId = meetingId, role = role
        )
        if (role == MainActivity.Role.OFFER) {
            peerConnectionBridge.sendSdpOffer(meetingId)
        }
    }

    private fun setup() {
        peerConnectionBridge = PeerConnectionBridge(peerConnectionFactory = peerConnectionFactory,
            signalingClient = signalingClient,
            peerConnectionCallbacks = object : PeerConnectionCallbacks {
                override fun displayVideoTrack(videoTrack: VideoTrack?) {
                    TODO("Not yet implemented")
                }

                override fun onAudioTrackAvailable(audioTrack: AudioTrack) {
                    TODO("Not yet implemented")
                }

            }
        )

        signalingClient.connectAndListen(meetingId)
    }

    override fun displayVideoTrack(videoTrack: VideoTrack?) {
        videoTrack?.addSink(remoteViewRenderer)
    }

    override fun onAudioTrackAvailable(audioTrack: AudioTrack) {
        this.audioTrack = audioTrack
    }

    fun muteAudioTrack() {
        audioTrack?.setEnabled(false)
    }

    override fun onConnectionEstablished() {
        Timber.d("connection established.")
    }

    override fun onOfferReceived(sessionDescription: SessionDescription) {
        peerConnectionBridge.onSdpOfferReceive(sessionDescription,meetingId)
    }

    override fun onAnswerReceived(sessionDescription: SessionDescription) {
        peerConnectionBridge.onSdpAnswerReceive(sessionDescription,meetingId)
    }

    override fun onCallEnded() {
        Timber.d("Call ended")
    }

}
