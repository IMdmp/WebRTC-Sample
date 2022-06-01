package com.imdmp.webrtcsample

import android.content.Context
import org.webrtc.*
import org.webrtc.PeerConnection.RTCConfiguration
import timber.log.Timber

interface PeerConnectionCallbacks {
    fun displayVideoTrack(videoTrack: VideoTrack?)
    fun onAudioTrackAvailable(audioTrack: AudioTrack)
}

class PeerConnectionBridge(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val signalingClient: SignalingClient,
    private val peerConnectionCallbacks: PeerConnectionCallbacks
) {
    lateinit var peerConnection: PeerConnection
    lateinit var capturer: CameraVideoCapturer

    var sendSdpCustomObserver = object : CustomSdpObserver {
        override fun onCreateSuccess(sdpOffer: SessionDescription) {
        }
    }

    var setLocalSdpObserver  = object :SdpObserver{
        override fun onCreateSuccess(p0: SessionDescription?) {
            Timber.d("loca oncreate succses")
        }

        override fun onSetSuccess() {
            Timber.d("local set success")
        }

        override fun onCreateFailure(p0: String?) {
           Timber.d("local create failure")
        }

        override fun onSetFailure(p0: String?) {
           Timber.d("local set failure ")
        }

    }
    var answerSdpCustomObserver = object : CustomSdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {
            TODO("Not yet implemented")
        }
    }

    var remoteSetupSdpObserver = object : SdpObserver{
        override fun onCreateSuccess(p0: SessionDescription?) {
            Timber.d("succesfully created remote.")
        }

        override fun onSetSuccess() {
            Timber.d("successfully set remote.")
        }

        override fun onCreateFailure(p0: String?) {
           Timber.d("failed creating remote.")
        }

        override fun onSetFailure(p0: String?) {
            Timber.d("failed setting remote.")
        }

    }

    fun createPeerConnection(
        iceServers: List<PeerConnection.IceServer>,
        meetingId: String,
        role: MainActivity.Role
    ) {
        val rtcConfig = RTCConfiguration(iceServers)

        peerConnection = peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : CustomObserver {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    Timber.d("on ice candidate.")
                    signalingClient.sendIceCandidate(iceCandidate, role, meetingId)
                }

                override fun onAddTrack(p0: RtpReceiver?, mediaStreams: Array<out MediaStream>) {
                    onTrackAdded(mediaStreams)
                }
            }
        )!!

    }

    private fun onTrackAdded(mediaStreams: Array<out MediaStream>) {
        val videoTrack: VideoTrack? = mediaStreams.firstNotNullOfOrNull {
            it.videoTracks.firstOrNull()
        }
        peerConnectionCallbacks.displayVideoTrack(videoTrack)

    }

    fun sendSdpOffer(meetingId: String) {
        peerConnection.createOffer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdpOffer: SessionDescription) {
                    peerConnection.setLocalDescription(setLocalSdpObserver, sdpOffer)
                    signalingClient.sendSdpOffer(sdpOffer, meetingId)
                }
            }, MediaConstraints()
        )
    }

    fun onSdpOfferReceive(
        sdpOffer: SessionDescription,
        meetingId: String
    ) {// Saving the received SDP-offer
        peerConnection.setRemoteDescription(remoteSetupSdpObserver, sdpOffer)
        sendSdpAnswer(meetingId)
    }

    // Forming and sending SDP-answer

    fun sendSdpAnswer(meetingId: String) {
        peerConnection.createAnswer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdpOffer: SessionDescription) {
                    peerConnection.setLocalDescription(sendSdpCustomObserver, sdpOffer)
                    signalingClient.sendSdpAnswer(sdpOffer, meetingId)
                }

            }, MediaConstraints()
        )
    }

    fun onSdpAnswerReceive(sdpAnswer: SessionDescription,meetingId: String) {
        Timber.d("on sdp answer receive")
        peerConnection.setRemoteDescription(sendSdpCustomObserver, sdpAnswer)
        sendSdpAnswer(meetingId)
    }

    fun onIceCandidateReceive(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)
    }

    private fun getLocalMediaStream(context: Context): MediaStream? {
        val stream = peerConnectionFactory.createLocalMediaStream("user")
        val audioTrack = getLocalAudioTrack()
        stream.addTrack(audioTrack)

        val videoTrack = getLocalVideoTrack(context)
        stream.addTrack(videoTrack)

        return stream

    }

    private fun getLocalAudioTrack(): AudioTrack {
        val audioConstraints = MediaConstraints()
        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        return peerConnectionFactory.createAudioTrack("user_audio", audioSource)
    }

    private fun getLocalVideoTrack(context: Context): VideoTrack {
        val cameraEnumerator = Camera2Enumerator(context)
        val camera = cameraEnumerator.deviceNames.firstOrNull {

            cameraEnumerator.isFrontFacing(it)

        } ?: cameraEnumerator.deviceNames.first()

        capturer = cameraEnumerator.createCapturer(camera, null)

        val surfaceTextureHelper = SurfaceTextureHelper.create(
            "CaptureThread",
            EglBase.create().eglBaseContext
        )

        val videoSource =

            peerConnectionFactory.createVideoSource(capturer.isScreencast ?: false)

        capturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        capturer.startCapture(1024, 720, 30)
        return peerConnectionFactory.createVideoTrack("user0_video", videoSource)
    }
}

interface CustomObserver : PeerConnection.Observer {
    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Timber.d("signaling change")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Timber.d("on ice connection chnage")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Timber.d("on ice connection receiving change")

    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Timber.d("on ice gathering change")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Timber.d("on ice candidates removed")
    }

    override fun onAddStream(p0: MediaStream?) {
        Timber.d("on add stream")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Timber.d("on remove stream")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Timber.d("on data channel")
    }

    override fun onRenegotiationNeeded() {
        Timber.d("on renegotiation needed")
    }
}

interface CustomSdpObserver : SdpObserver {
    override fun onSetSuccess() {
        Timber.d("on set succces")

    }

    override fun onCreateFailure(p0: String?) {
        Timber.d("on create failure")
    }

    override fun onSetFailure(p0: String?) {
        Timber.d("on set failure")

    }

}