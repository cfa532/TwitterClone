package com.example.twitterclone.viewmodel

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class TweetFeedEvent {
    data class AddTweet(val tweet: Tweet) : TweetFeedEvent()
    data class findTweet(val tweetId: MimeiId) : TweetFeedEvent()
}

object TweetFeedEventBus {
    val events = MutableSharedFlow<TweetFeedEvent>()
}