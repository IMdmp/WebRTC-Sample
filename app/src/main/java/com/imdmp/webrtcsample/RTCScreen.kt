package com.imdmp.webrtcsample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.AngleDown
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack


@Composable
fun RTCScreen(rtcListener: RTCActivity.RTCViewListener) {
    ConstraintLayout {
        val (remoteView, localView) = createRefs()

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(localView) {

                },
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    rtcListener.localFinishedRendering(this)
                }
            }) {

        }

        AndroidView(
            modifier = Modifier
                .width(124.dp)
                .height(164.dp)
                .constrainAs(remoteView) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }, factory = { context ->
                SurfaceViewRenderer(context).apply {
                    rtcListener.remoteFinishedRendering(this)
                }
            })

        Row {
            //mic
            //vid
            //end
            //switch camera
            //audio output

            //sample
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.AngleDown,
                    tint = Color.White,
                    contentDescription = ""
                )
            }
        }


    }
}
