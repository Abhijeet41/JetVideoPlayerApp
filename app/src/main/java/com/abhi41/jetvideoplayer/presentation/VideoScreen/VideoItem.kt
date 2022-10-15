package com.abhi41.jetvideoplayer.presentation.VideoScreen

import android.net.Uri
import androidx.media3.common.MediaItem

data class VideoItem(
    val contentUri: Uri,
    val mediaItem: MediaItem, //this comes from exoplayer media library
    val name: String
)
