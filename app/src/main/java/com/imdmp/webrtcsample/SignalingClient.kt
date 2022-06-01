package com.imdmp.webrtcsample

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface SignalingClient {
    fun connectAndListen(meetingID: String)
    fun sendSdpOffer(sdpOffer: SessionDescription,meetingID: String)
    fun sendSdpAnswer(sdpAnswer: SessionDescription,meetingID: String)
    fun sendIceCandidate(
        candidate: IceCandidate, role: MainActivity.Role, meetingID: String
    )
}
