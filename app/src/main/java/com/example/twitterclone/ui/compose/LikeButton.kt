package com.example.twitterclone.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.twitterclone.R
import com.example.twitterclone.model.HproseInstance.bookmarkTweet
import com.example.twitterclone.model.HproseInstance.likeTweet
import com.example.twitterclone.model.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


@Composable
fun LikeButton(tweet: Tweet) {
    IconButton(onClick = {
        runBlocking {
            withContext(Dispatchers.IO) {
                likeTweet(tweet)
            }
        }
    }) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "Like",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${tweet.likeCount}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun BookmarkButton(tweet: Tweet) {
    IconButton(onClick = {
        runBlocking {
            withContext(Dispatchers.IO) {
                bookmarkTweet(tweet)
            }
        }
    }) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Like",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${tweet.bookmarkCount}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}