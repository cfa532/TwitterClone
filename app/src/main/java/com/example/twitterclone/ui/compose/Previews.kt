package com.example.twitterclone.ui.compose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.twitterclone.R
import com.example.twitterclone.network.Gadget.detectFileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//sealed class MediaItem {
//    data class Image(val url: String) : MediaItem()
//    data class Video(val thumbnailUrl: String) : MediaItem()
//    data object Audio : MediaItem()
//}
data class MediaItem( val url: String )

@Composable
fun MediaPreviewGrid(mediaItems: List<MediaItem>) {
    val maxItems = 4 // 2 rows * 2 columns = 4 items
    val limitedMediaList = mediaItems.take(maxItems)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(limitedMediaList) { mediaItem ->
            Row(
                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MediaItemPreview(mediaItem)
                // Add another MediaItemPreview for the second column here
            }
        }
    }
}

@Composable
fun MediaItemPreview( mediaItem: MediaItem ) {
    val fileType = remember(mediaItem.url) {
        mutableStateOf<String?>(null)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mediaItem.url) {
        coroutineScope.launch {
            val detectedFileType = withContext(Dispatchers.IO) {
                detectFileType(mediaItem.url)
            }
            fileType.value = detectedFileType
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        when (fileType.value) {
            "image" -> {
                AsyncImage(
                    model = mediaItem.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(200.dp)
                )
            }
            "video" -> {
                // Implement video player here
                VideoPreview(url = mediaItem.url)
            }
            "audio" -> {
                // Implement audio player here
//                AudioPreview()
                VideoPreview(url = mediaItem.url)
            }
            else -> {
                // Handle unknown file type
                println("unknown file type ${fileType.value}")
            }
        }
    }
}


@Composable
fun VideoPreview(url: String) {
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(androidx.media3.common.MediaItem.fromUri(Uri.parse(url)))
        prepare()
    }

    AndroidView(
        factory = { PlayerView(context).apply { player = exoPlayer } },
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun AudioPreview() {
    Image(
        painter = painterResource(id = R.drawable.ic_headphones), // Replace with your audio placeholder image
        contentDescription = null,
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}