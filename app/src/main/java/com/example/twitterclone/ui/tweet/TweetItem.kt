package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetItem(
    tweet: Tweet,
) {
    val viewModel = TweetViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Use a Row to align author name and potential verification badge
        Row(verticalAlignment= Alignment.CenterVertically) {
            Text(text = "No One", style = MaterialTheme.typography.bodyMedium)
            // Add a verified badge icon if the user is verified (assuming you have a way to check this)
            // if (author.isVerified) {
            //     Icon(imageVector = Icons.Filled.Verified, contentDescription = "Verified")
            // }
        }
        Spacer(modifier = Modifier.height(4.dp))
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