package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.HproseInstance.getMediaUrl
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.ui.compose.BookmarkButton
import com.example.twitterclone.ui.compose.CircularImage
import com.example.twitterclone.ui.compose.CommentButton
import com.example.twitterclone.ui.compose.LikeButton
import com.example.twitterclone.ui.compose.MediaItem
import com.example.twitterclone.ui.compose.MediaPreviewGrid
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetItem(
    tweet: Tweet,
    viewModel: TweetViewModel = TweetViewModel()
) {
    val author by viewModel.author.collectAsState()
    viewModel.setTweet(tweet).also { viewModel.setTweetAuthor() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        // Use a Row to align author name and potential verification badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularImage(
                model = getMediaUrl(author?.avatar),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(text = author?.name ?: "No One", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = tweet.content, style = MaterialTheme.typography.bodyMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp) // Set a specific height for the grid
        ) {
            val mediaItems = tweet.attachments?.map {
                MediaItem(getMediaUrl(it).toString())
            }
            mediaItems?.let { MediaPreviewGrid(it) }
        }
        // Use a Row to display likes and bookmarks horizontally
        tweet.let {
            Row {
                LikeButton(it, viewModel)
                Spacer(modifier = Modifier.width(8.dp)) // Add some space between the two texts
                BookmarkButton(it)
                Spacer(modifier = Modifier.width(8.dp))
                CommentButton(it)
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