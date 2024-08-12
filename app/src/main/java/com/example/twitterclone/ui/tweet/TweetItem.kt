package com.example.twitterclone.ui.tweet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetItem(
    tweet: Tweet,
    viewModel: TweetViewModel = TweetViewModel()
) {
    viewModel.setTweet(tweet)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Content body
        if (tweet.originalTweetId != null) {
            if (tweet.content == "") {
                // this is a retweet of another tweet.
                Text(
                    text = "Forwarded by you",
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 12.dp)
                )

                tweet.originalTweet?.let {
                    TweetBody(it, viewModel)
                }
            } else {
                // retweet with comments
                TweetHeader(tweet, viewModel)
                Text(
                    text = tweet.content,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Box(modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .border(BorderStroke(2.dp, Color.Gray))
                ) {
                    TweetBody(tweet, viewModel)
                }
            }
        } else {
            // original tweet by current user.
            TweetBody(tweet, viewModel)
        }
    }
}