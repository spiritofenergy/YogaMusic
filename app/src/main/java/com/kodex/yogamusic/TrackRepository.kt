package com.kodex.yogamusic

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kodex.yogamusic.presentation.models.Track
import com.kodex.yogamusic.presentation.models.toTrack
import com.kodex.yogamusic.utils.Constants
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TrackRepository {
    private val storage = Firebase.storage
    private val albumArtRef = storage.reference.child(Constants.ALBUM_ART)
    private val trackReference = storage.reference
    var mAuth = FirebaseAuth.getInstance()

    suspend fun getTracks() = suspendCoroutine<List<Track>> {

        val albumList = mutableListOf<Track>()
        try {
            Firebase.firestore.collection(Constants.TRACKS)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var index = 0
                        task.result.forEach { document ->
                            val imageUrl =
                                albumArtRef.child(document.getString(Constants.ALBUM_ART)!!)
                            val trackUrl =
                                trackReference.child(document.getString(Constants.FILENAME)!!)
                            imageUrl.downloadUrl.addOnSuccessListener { imgDownloadUrl ->
                                trackUrl.downloadUrl.addOnSuccessListener { trackDownloadUrl ->
                                    albumList.add(
                                        document.toTrack(
                                            index,
                                            imgDownloadUrl.toString(),
                                            trackDownloadUrl.toString()
                                        )
                                    )
                                    if (index == task.result.size() - 1) {
                                        it.resume(albumList)
                                    }
                                    index++
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.d("trackSync", "failed : ${e.message}")
        }

    }
}



