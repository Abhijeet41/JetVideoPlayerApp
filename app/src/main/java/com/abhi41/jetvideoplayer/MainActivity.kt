package com.abhi41.jetvideoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.abhi41.jetvideoplayer.presentation.VideoScreen.MainViewModel
import com.abhi41.jetvideoplayer.presentation.VideoScreen.VideoItem
import com.abhi41.jetvideoplayer.ui.theme.JetVideoPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetVideoPlayerTheme {

                val viewModel = hiltViewModel<MainViewModel>()
                val videoItems by viewModel.videoItems.collectAsState()

                val selectVideoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = { uri ->
                        uri?.let {
                            viewModel.addVideoUri(uri)
                            //simpleway
                            //viewModel.addVideoUri(it)
                        }
                    }
                )

                var lifecycle by remember {
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        lifecycle = event
                    }
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 12f),
                        factory = { context ->
                            PlayerView(context).also { playerView: PlayerView ->
                                playerView.player = viewModel.player
                            }
                        },
                        update = {
                            when (lifecycle) {
                                Lifecycle.Event.ON_PAUSE -> {
                                    it.onPause()
                                    it.player?.pause()
                                }
                                Lifecycle.Event.ON_RESUME -> {
                                    it.onResume()
                                }
                                else -> Unit
                            }
                        }

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = {
                        selectVideoLauncher.launch("video/*")
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Select video"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(videoItems) { item: VideoItem ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.playVideo(item.contentUri)
                                    }
                                    .padding(16.dp),
                                text = item.name,
                            )
                        }
                    }
                }

            }
        }
    }
}
