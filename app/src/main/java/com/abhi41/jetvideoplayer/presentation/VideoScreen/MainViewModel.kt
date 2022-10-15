package com.abhi41.jetvideoplayer.presentation.VideoScreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.abhi41.jetvideoplayer.util.MetaDataReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player, //this for play the video pause the video or next, prev etc
    private val metaDataReadr: MetaDataReader
) : ViewModel() {

    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    val videoItems = videoUris.map { uris: List<Uri> ->
        uris.map { uri: Uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReadr.getMetaDataFromUri(uri)?.fileName ?: "No Name"
            )
        }
    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000), emptyList())

    init { //this will called before play the video
        player.prepare()
    }

    fun addVideoUri(uri: Uri){
        savedStateHandle["videoUris"] = videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun playVideo(uri: Uri){
        player.setMediaItem(
            videoItems.value.find { videoItem->
                videoItem.contentUri == uri
            }?.mediaItem ?: return

            //simple way
          /*  videoItems.value.forEach { videoItem->
                if (videoItem.contentUri == uri){
                     videoItem.mediaItem
                }else{
                    return
                }
            }*/
        )
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }


}