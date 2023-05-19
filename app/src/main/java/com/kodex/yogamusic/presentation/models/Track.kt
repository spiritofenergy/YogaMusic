package com.kodex.yogamusic.presentation.models

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.kodex.yogamusic.utils.Constants

data class Track(
    val img: String,
    val index: Int,
    val songTitle: String,
    val artist: String,
    val trackUrl: String,
    var isPlaying: Boolean,
    val fileName: String
)
fun QueryDocumentSnapshot.toTrack(index: Int, imgUrl: String, trackUrl: String): Track {
    return Track(
        img = imgUrl,
        songTitle = this.getString(Constants.NAME)?: "",
        artist = this.getString(Constants.ARTIST)?: "",
        fileName = this.getString(Constants.FILENAME)?: "",
        isPlaying = false,
        index = index,
        trackUrl = trackUrl
    )
}