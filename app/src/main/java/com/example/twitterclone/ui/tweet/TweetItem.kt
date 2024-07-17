package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetItem(
    tweet: Tweet,
    author: User,
    originalTweet: Tweet?,
    viewModel: TweetViewModel,
    currentUserMid: MimeiId
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = author.name, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = tweet.content, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${tweet.likeCount} Likes", style = MaterialTheme.typography.labelSmall)
        Text(text = "${tweet.bookmarkCount} Bookmarks", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = { viewModel.likeTweet(tweet.mid) }) {
                Text("Like")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* Handle reply action */ }) {
                Text("Reply")
            }
        }
        originalTweet?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Retweet of:", style = MaterialTheme.typography.labelSmall)
            TweetItem(
                tweet = it,
                author = author,
                originalTweet = null,
                viewModel = viewModel,
                currentUserMid = currentUserMid
            )
        }
    }
}