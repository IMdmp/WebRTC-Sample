package com.imdmp.webrtcsample

import android.content.Context
import org.webrtc.*
import org.webrtc.PeerConnection.RTCConfiguration
import timber.log.Timber

interface PeerConnectionCallbacks{
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

    var customSdpObserver = object : CustomSdpObserver {
        override fun onCreateSuccess(sdpOffer: SessionDescription) {
        }
    }

    fun createPeerConnection(iceServers: List<PeerConnection.IceServer>) {
        val rtcConfig = RTCConfiguration(iceServers)

        peerConnection = peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : CustomObserver {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    signalingClient.sendIceCandidate(iceCandidate)
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

    fun sendSdpOffer() {
        peerConnection.createOffer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdpOffer: SessionDescription) {
                    peerConnection.setLocalDescription(customSdpObserver, sdpOffer)
                    signalingClient.sendSdpOffer(sdpOffer)
                }
            }, MediaConstraints()
        )
    }

    fun onSdpOfferReceive(sdpOffer: SessionDescription) {// Saving the received SDP-offer
        peerConnection.setRemoteDescription(customSdpObserver, sdpOffer)
        sendSdpAnswer()
    }

    // Forming and sending SDP-answer

    fun sendSdpAnswer() {
        peerConnection.createAnswer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdpOffer: SessionDescription) {
                    peerConnection.setLocalDescription(customSdpObserver, sdpOffer)
                    signalingClient.sendSdpAnswer(sdpOffer)
                }

            }, MediaConstraints()
        )
    }

    fun onSdpAnswerReceive(sdpAnswer: SessionDescription) {
        peerConnection.setRemoteDescription(customSdpObserver, sdpAnswer)
        sendSdpAnswer()
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