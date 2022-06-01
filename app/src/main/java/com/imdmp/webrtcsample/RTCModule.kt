package com.imdmp.webrtcsample

import android.app.Application
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory

object RTCModule {


    private fun provideRootEglBase(): EglBase {
        return EglBase.create()

    }

    fun buildPeerConnectionFactory(application:Application): PeerConnectionFactory {
        val eglBase = provideRootEglBase()
        initPeerConnectionFactory(application)
        return PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            })
            .createPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory(context: Application) {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    fun provideSignalingClient(signalingClientListener: SignalingClientListener): SignalingClient{
        return FireStoreSignalingClientImpl(signalingClientListener)
    }

}
