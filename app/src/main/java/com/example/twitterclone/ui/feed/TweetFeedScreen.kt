package com.example.twitterclone.ui.feed
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.ui.tweet.TweetItem


@Composable
fun TweetFeedScreen(viewModel: TweetFeedViewModel = viewModel()) {
    val tweets = viewModel.tweets
    val context = LocalContext.current

    LazyColumn {
        items(tweets) { tweet ->
            TweetItem(tweet = tweet, onMediaClick = { uri ->
                // Handle media click, e.g., open a media player or download the file
                handleMediaClick(context, uri)
            })
        }
    }
}

fun handleMediaClick(context: Context, uri: Uri) {
    // Implement logic to handle media click, e.g., open a media player or download the file
    // This could involve starting an activity with an intent to view/play the media
}