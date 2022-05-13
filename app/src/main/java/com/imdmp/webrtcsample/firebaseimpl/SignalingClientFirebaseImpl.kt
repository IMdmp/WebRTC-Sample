package com.imdmp.webrtcsample.firebaseimpl

import com.imdmp.webrtcsample.SignalingClient
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SignalingClientFirebaseImpl : SignalingClient {
    override fun sendSdpOffer(sdpOffer: SessionDescription) {

    }

    override fun sendSdpAnswer(sdpAnswer: SessionDescription) {
        TODO("Not yet implemented")
    }

    override fun sendIceCandidate(candidate: IceCandidate) {
        TODO("Not yet implemented")
    }
}
