package com.imdmp.webrtcsample

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class FireStoreSignalingClientImpl : SignalingClient {
    override fun sendSdpOffer(sdpOffer: SessionDescription) {
        TODO("Not yet implemented")
    }

    override fun sendSdpAnswer(sdpAnswer: SessionDescription) {
        TODO("Not yet implemented")
    }

    override fun sendIceCandidate(candidate: IceCandidate) {
        TODO("Not yet implemented")
    }
}