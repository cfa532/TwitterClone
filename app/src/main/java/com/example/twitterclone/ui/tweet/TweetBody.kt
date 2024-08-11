package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.twitterclone.ui.compose.RetweetButton
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetBody(tweet: Tweet, viewModel: TweetViewModel) {
    // Tweet Header
    TweetHeader(tweet)

    Text(text = tweet.content, style = MaterialTheme.typography.bodyMedium)

    // attached media files
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
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LikeButton(it, viewModel)
            Spacer(modifier = Modifier.width(8.dp)) // Add some space between the two texts
            BookmarkButton(it, viewModel)
            Spacer(modifier = Modifier.width(8.dp))
            CommentButton(it, viewModel)
            Spacer(modifier = Modifier.width(8.dp))
            RetweetButton(it, viewModel)
        }
    }
}

@Composable
fun TweetHeader(tweet: Tweet) {
    // Use a Row to align author name and potential verification badge
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularImage(
            model = getMediaUrl(tweet.author?.avatar),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
        Text(text = tweet.author?.name ?: "No One", style = MaterialTheme.typography.bodyMedium)
    }
}