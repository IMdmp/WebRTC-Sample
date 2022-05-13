package com.imdmp.webrtcsample.custom

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber

interface CustomSdpObserver : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription)

    override fun onSetSuccess() {
        Timber.d("set success")
    }

    override fun onCreateFailure(p0: String?) {
        Timber.d("onCreateFailure")

    }

    override fun onSetFailure(p0: String?) {
        Timber.d("onSetFailure")

    }
}
