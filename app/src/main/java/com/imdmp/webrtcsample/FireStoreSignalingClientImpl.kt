package com.imdmp.webrtcsample

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.imdmp.webrtcsample.base.FirestoreConstants
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber

class FireStoreSignalingClientImpl(private val listener: SignalingClientListener) :
    SignalingClient {
    private val db = Firebase.firestore
    private var sdpType = ""
    override fun sendSdpOffer(sdpOffer: SessionDescription, meetingID: String) {
        val offer = hashMapOf(
            "sdp" to sdpOffer?.description,
            "type" to sdpOffer?.type
        )
        db.collection("calls").document(meetingID)
            .set(offer)
            .addOnSuccessListener {
                Timber.d("DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Timber.d("Error adding document", e)
            }
    }

    override fun sendSdpAnswer(sdpAnswer: SessionDescription, meetingID: String) {
        val answer = hashMapOf(
            "sdp" to sdpAnswer?.description,
            "type" to sdpAnswer?.type
        )
        db.collection("calls").document(meetingID)
            .set(answer)
            .addOnSuccessListener {
                Timber.d("able to store document")
            }
            .addOnFailureListener { e ->
                Timber.d("Error adding document", e)
            }
    }

    override fun sendIceCandidate(
        candidate: IceCandidate,
        role: MainActivity.Role,
        meetingID: String
    ) {
        val candidateData = hashMapOf(
            "serverUrl" to candidate?.serverUrl,
            "sdpMid" to candidate?.sdpMid,
            "sdpMLineIndex" to candidate?.sdpMLineIndex,
            "sdpCandidate" to candidate?.sdp,
            "role" to role.getStr()
        )

        db.collection(FirestoreConstants.COLLECTION_NAME)
            .document(meetingID).collection("candidates").document(role.getStr())
            .set(candidateData as Map<String, Any>)
            .addOnSuccessListener {
                Timber.d("sendIceCandidate: Success")
            }
            .addOnFailureListener {
                Timber.e("sendIceCandidate: Error $it")
            }
    }

    override fun connectAndListen(meetingID: String) {
        db.enableNetwork().addOnSuccessListener {
            listener.onConnectionEstablished()
        }

        db.collection("calls")
            .document(meetingID)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e("listen:error", e)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    snapshot.data?.let { data ->
                        if (data.containsKey(FirestoreConstants.TYPE)) {
                            when (data.getValue(FirestoreConstants.TYPE)) {
                                "OFFER" -> {
                                    listener.onOfferReceived(
                                        SessionDescription(
                                            SessionDescription.Type.OFFER, data["sdp"].toString()
                                        )
                                    )
                                    sdpType = "Offer"
                                }
                                "ANSWER" -> {
                                    listener.onAnswerReceived(
                                        SessionDescription(
                                            SessionDescription.Type.ANSWER, data["sdp"].toString()
                                        )
                                    )
                                    sdpType = "Answer"
                                }
                            }
                        }
                    }
                }
            }
    }
}

interface SignalingClientListener {
    fun onConnectionEstablished()
    fun onOfferReceived(sessionDescription: SessionDescription)
    fun onAnswerReceived(sessionDescription: SessionDescription)
    fun onCallEnded()

}