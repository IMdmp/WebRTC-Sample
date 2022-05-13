package com.imdmp.webrtcsample

import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

class RTCClient {
    private val rootEglBase: EglBase = EglBase.create()

    fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext, null)
    }
}
