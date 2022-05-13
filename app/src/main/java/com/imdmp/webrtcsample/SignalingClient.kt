package com.imdmp.webrtcsample

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface SignalingClient {

    fun sendSdpOffer(sdpOffer:SessionDescription)
    fun sendSdpAnswer(sdpAnswer: SessionDescription)
    fun sendIceCandidate(candidate: IceCandidate)
}
