package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.twitterclone.R
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.network.HproseInstance.getImageSource
import com.example.twitterclone.ui.compose.CircularImage
import com.example.twitterclone.viewmodel.TweetViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun TweetItem(
    tweet: Tweet,
) {
    val viewModel = TweetViewModel()
    val author = runBlocking { viewModel.getAuthor(tweet.authorId) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        // Use a Row to align author name and potential verification badge
        Row(verticalAlignment= Alignment.CenterVertically) {
            CircularImage(
                model = getImageSource(author?.avatar),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(text = author?.name?: "No One", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = tweet.content, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Use a Row to display likes and bookmarks horizontally
        Row {
            Text(text = "${tweet.likeCount} Likes", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(8.dp)) // Add some space between the two texts
            Text(text = "${tweet.bookmarkCount} Bookmarks", style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row {
            // Consider using OutlinedButton for a less prominent look for secondary actions
            OutlinedButton(onClick = { tweet.mid?.let { viewModel.likeTweet(it) } }) {
                Text("Like")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { /* Handle reply action */ }) {
                Text("Reply")
            }
        }

//        originalTweet?.let {
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = "Retweet of:", style = MaterialTheme.typography.labelSmall)
//            // Pass null for originalTweet to avoid infinite recursion
//            TweetItem(
//                tweet = it,
//                author = author,
//                originalTweet = null,
//                viewModel = viewModel,
//                currentUserMid = currentUserMid
//            )
//        }
    }
}