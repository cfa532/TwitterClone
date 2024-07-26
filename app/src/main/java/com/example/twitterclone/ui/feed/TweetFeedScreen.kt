package com.example.twitterclone.ui.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.ui.tweet.TweetItem
import com.example.twitterclone.viewmodel.TweetFeedViewModel
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetFeedScreen(
    tweetFeedViewModel: TweetFeedViewModel = viewModel(),
    tweetViewModel: TweetViewModel = viewModel(),
    currentUserMid: MimeiId
) {
    val tweets = tweetFeedViewModel.tweets.collectAsState().value

    LazyColumn {
        items(tweets) { tweet ->
            TweetItem(tweet)
        }
    }
}

