package com.example.twitterclone.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.twitterclone.R

@Composable
fun AppIcon() {
    CircularImage(
        model = R.drawable.ic_app_icon,
        contentDescription = "App Icon",
        modifier = Modifier.size(40.dp)
    )
}