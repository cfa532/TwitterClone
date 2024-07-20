package com.example.twitterclone.repository

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet

class TweetRepository {
    private val tweets = mutableListOf<Tweet>()

    fun addTweet(tweet: Tweet) {
        tweets.add(tweet)
    }

    fun getTweet(tweetMid: MimeiId): Tweet {
        return tweets.find { it.mid == tweetMid } ?: throw IllegalArgumentException("Tweet not found")
    }

    fun updateTweet(updatedTweet: Tweet) {
        val index = tweets.indexOfFirst { it.mid == updatedTweet.mid }
        if (index != -1) {
            tweets[index] = updatedTweet
        } else {
            throw IllegalArgumentException("Tweet not found")
        }
    }

    fun deleteTweet(tweetMid: MimeiId) {
        tweets.removeIf { it.mid == tweetMid }
    }

    fun getTweets(): List<Tweet> {
        return tweets
    }
}