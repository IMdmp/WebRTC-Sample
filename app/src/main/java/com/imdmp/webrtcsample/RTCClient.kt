package com.imdmp.webrtcsample

import com.imdmp.webrtcsample.custom.CustomSdpObserver
import org.webrtc.*

class RTCClient(
    private val signalingClient: SignalingClient,
    private val peerConnectionFactory: PeerConnectionFactory,
    private val rootEglBase: EglBase
) {

    lateinit var peerConnection: PeerConnection

    fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext, null)
    }

    // Offer
    fun sendSdpOffer(sdbObserver: SdpObserver) {
        peerConnection.createOffer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    peerConnection.setLocalDescription(sdbObserver, sdp)
                    signalingClient.sendSdpOffer(sdp)
                }
            }, MediaConstraints()

        )

    }

    fun onSdpAnswerReceive(sdpAnswer: SessionDescription, sdpObserver: SdpObserver) {
        peerConnection.setRemoteDescription(sdpObserver, sdpAnswer)
        sendSdpAnswer(sdpObserver)
    }

    // Answer

    // Forming and sending SDP-answer
    fun sendSdpAnswer(sdpObserver: SdpObserver) {

        peerConnection.createAnswer(
            object : CustomSdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    peerConnection.setLocalDescription(sdpObserver, sdp)
                    signalingClient.sendSdpAnswer(sdp)
                }
            }, MediaConstraints()
        )

    }

    fun onSdpOfferReceive(
        sdpOffer: SessionDescription,
        sdpObserver: SdpObserver
    ) {// Saving the received SDP-offer
        peerConnection.setRemoteDescription(sdpObserver, sdpOffer)
        sendSdpAnswer(sdpObserver)
    }

    fun onIceCandidateReceive(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)
    }
}
