package com.example.twitterclone

import com.example.twitterclone.model.Tweet
import kotlinx.serialization.Serializable

class Navigator {

}

@Serializable
object TweetFeed

@Serializable
data class TweetView( val tweet: Tweet)

