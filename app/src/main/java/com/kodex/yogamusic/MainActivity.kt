package com.kodex.yogamusic

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.kodex.yogamusic.presentation.models.MusicPlayerOption
import com.kodex.yogamusic.presentation.models.Track
import com.kodex.yogamusic.presentation.sign_in.GoogleAuthUiClient
import com.kodex.yogamusic.presentation.sign_in.SignInScreen
import com.kodex.yogamusic.presentation.sign_in.SignInViewModel
import com.kodex.yogamusic.ui.composables.AlbumList
import com.kodex.yogamusic.ui.composables.LoadingScreen
import com.kodex.yogamusic.ui.composables.Player
import com.kodex.yogamusic.ui.composables.Title
import com.kodex.yogamusic.ui.composables.TrackDetailDialog
import com.kodex.yogamusic.ui.composables.TurnTable
import com.kodex.yogamusic.ui.theme.JakeBoxComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMusicButtonClick {

    private val isPlaying = mutableStateOf(false) // is music current being played
    private var trackList = listOf<Track>() // retrieve song list
    private lateinit var currentSong: MutableState<Track>// currently playing song
    private val clickedSong: MutableState<Track?> = mutableStateOf(null)// currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState = mutableStateOf(false)// turns turntable arm
    private val isBuffering = mutableStateOf(false)
    private val isTurntableArmFinished =
        mutableStateOf(false) // lets us know the turntable Arm rotation is finished
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables
    private lateinit var mediaPlayer: MediaPlayer
    private val tracksViewModel : TrackViewModel by viewModels()
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setContent {
            JakeBoxComposeTheme() {
                Surface(modifier = Modifier.fillMaxSize()) {
                    listState = rememberLazyListState()
                    coroutineScope = rememberCoroutineScope()
                    val openDialog = remember { mutableStateOf(false) }
                    val trackList by tracksViewModel.trackList.observeAsState()
                    Log.d("trackList", "tracksViewModel: $trackList")
                    if (trackList?.isNotEmpty() == true) {
                        MainContent(
                            isPlaying = isPlaying,
                            currentSong,
                            listState,
                            onMusicPlayerClick = this@MainActivity,
                            currentSongIndex,
                            turntableArmState,
                            isTurntableArmFinished,
                            isBuffering = isBuffering,
                            trackList!!
                        ) { song ->
                            clickedSong.value = song
                            openDialog.value = true
                        }
                        TrackDetailDialog(track = clickedSong, openDialog = openDialog)
                    } else {
                        LoadingScreen()
                     }
                }
            }
        }
    }

    private fun observeViewModel() {
        tracksViewModel.trackList.observe(this) { list ->
            if (list.isNotEmpty()) {
                trackList = list
                Log.d("trackList", "$list")
                currentSong = mutableStateOf(list.first())
            }
        }
    }

    private fun play() {
        try {
            if (this::mediaPlayer.isInitialized && isPlaying.value) {
                mediaPlayer.stop()
                mediaPlayer.release()
                isPlaying.value = false
                turntableArmState.value = false
                isTurntableArmFinished.value = false
            }
            isBuffering.value = true
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(currentSong.value.trackUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isBuffering.value = false
                isPlaying.value = true
                if (!turntableArmState.value) {
                    turntableArmState.value = true
                }
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateList() {
        coroutineScope.launch {
            if (isPlaying.value) {
                currentSong.value.isPlaying = true
            }
            listState.animateScrollToItem(
                currentSong.value.index
            )
        }
    }

    override fun onMusicButtonClick(command: MusicPlayerOption) {
        when (command) {
            MusicPlayerOption.Skip -> {
                // check list
                var nextSongIndex = currentSong.value.index + 1 // increment next
                // if current song is last song in the tracklist (track list starts at 0)
                if (currentSong.value.index == trackList.size - 1) {
                    nextSongIndex = 0 // go back to first song
                    if (isPlaying.value) {
                        currentSongIndex.value = 0 // playing song is first song in list
                    }
                } else {
                    currentSongIndex.value++ // increment song index
                }
                currentSong.value = trackList[nextSongIndex]

                if (isPlaying.value) {
                    play()
                }
                updateList()
            }
            MusicPlayerOption.Previous -> {

                var previousSongIndex = currentSong.value.index - 1 // increment previous
                // if current song is first song in the tracklist (track list starts at 0)
                if (currentSong.value.index == 0) {
                    previousSongIndex = trackList.lastIndex // go to last song in list
                    if (isPlaying.value) {
                        currentSongIndex.value =
                            trackList.lastIndex // last song is now playing song
                    }
                } else {
                    currentSongIndex.value-- // decrement current song
                }
                currentSong.value = trackList[previousSongIndex]

                if (isPlaying.value) {
                    play()
                }

                updateList()
            }

            MusicPlayerOption.Play -> {
                currentSong.value.isPlaying =
                    !isPlaying.value // confirms whether current song is played or paused
                currentSongIndex.value = currentSong.value.index //confirms current song Index
                try {
                    if (this::mediaPlayer.isInitialized && isPlaying.value) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        isPlaying.value = false
                    } else play()
                } catch (e: Exception) {
                    mediaPlayer.release()
                    isPlaying.value = false
                }
            }
        }
    }
}
@Composable
fun MainContent(
    isPlaying: MutableState<Boolean>,
    album: MutableState<Track>,
    listState: LazyListState,
    onMusicPlayerClick: OnMusicButtonClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
    albums: List<Track>,
    onTrackItemClick: (Track) -> Unit,
) {
    Column {
        Title()
        AlbumList(
            isPlaying,
            listState,
            currentSongIndex,
            R.drawable.ic_baseline_pause_24,
            albums,
            onTrackItemClick
        )
        TurnTable(isPlaying, turntableArmState, isTurntableArmFinished)
        Player(
            album, isPlaying,
            onMusicPlayerClick = onMusicPlayerClick,
            isTurntableArmFinished = isTurntableArmFinished,
            isBuffering = isBuffering
        )
    }
}

interface OnMusicButtonClick {
    fun onMusicButtonClick(command: MusicPlayerOption)
}
