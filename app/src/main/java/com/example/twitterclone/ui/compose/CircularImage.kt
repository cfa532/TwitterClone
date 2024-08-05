package com.example.twitterclone.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CircularImage(model: Any, contentDescription: String?, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(model),
        contentDescription = contentDescription,
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}