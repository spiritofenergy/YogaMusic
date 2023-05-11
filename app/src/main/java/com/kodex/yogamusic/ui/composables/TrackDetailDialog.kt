package com.kodex.yogamusic.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kodex.yogamusic.R
import com.kodex.yogamusic.models.Track
import com.kodex.yogamusic.ui.theme.dialogColor

@Composable
fun TrackDetailDialog(track: MutableState<Track?>, openDialog: MutableState<Boolean>) {
    if (openDialog.value && track.value != null) {
        val track = track.value!!
        AlertDialog(
            backgroundColor = dialogColor,
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = track.songTitle)
            },

            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        onClick = { openDialog.value = false }
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrack(){
  //  TrackDetailDialog(track = track.value!!, openDialog = )
 }
