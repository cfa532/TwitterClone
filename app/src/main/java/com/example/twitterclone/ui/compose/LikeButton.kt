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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.twitterclone.R
import com.example.twitterclone.model.HproseInstance.bookmarkTweet
import com.example.twitterclone.model.HproseInstance.likeTweet
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.viewmodel.TweetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CommentButton(tweet: Tweet) {
    val commentCount by remember { mutableIntStateOf(tweet.commentCount) }
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
            }
        }
    }) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notice),
                contentDescription = "comments",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "$commentCount", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun LikeButton(tweet: Tweet, viewModel: TweetViewModel) {
    var likeCount by remember { mutableIntStateOf(tweet.likeCount) }
    var hasLiked by remember { mutableStateOf(tweet.hasLiked) }

    IconButton(onClick = {
        viewModel.likeTweet(tweet)
        likeCount = tweet.likeCount
        hasLiked = tweet.hasLiked
        println("Like button pressed. Current tweet: $likeCount")
    }) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = if (hasLiked) R.drawable.ic_heart_fill else R.drawable.ic_heart),
                contentDescription = "Like",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = if (hasLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$likeCount",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun BookmarkButton(tweet: Tweet) {
    var bookmarkCount by remember { mutableIntStateOf(tweet.bookmarkCount) }
    var hasBookmarked by remember { mutableStateOf(tweet.hasBookmarked) }
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                bookmarkTweet(tweet)
                bookmarkCount = tweet.bookmarkCount
                hasBookmarked = tweet.hasBookmarked
            }
        }
    }) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = if (hasBookmarked) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark),
                contentDescription = "Like",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = if (hasBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$bookmarkCount",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}