package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetItem(tweet: Tweet, viewModel: TweetViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = tweet.author, style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = tweet.content, style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${tweet.likeCount} Likes", style = MaterialTheme.typography.caption)
        Text(text = "${tweet.bookmarkCount} Bookmarks", style = MaterialTheme.typography.caption)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.bookmarkTweet(tweet.mid) }) {
            Text("Bookmark")
        }
    }
}