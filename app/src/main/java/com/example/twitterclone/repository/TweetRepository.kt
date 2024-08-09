package com.example.twitterclone.repository

import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import hprose.client.HproseClient

class TweetRepository (
) {
    private val hproseClient = HproseInstance
    private val tweets = mutableListOf<Tweet>()

    fun addTweet(tweet: Tweet): List<Tweet> {
        tweets.add(tweet)
        return tweets
    }

    fun getTweet(tweetMid: MimeiId): Tweet {
        hproseClient.appUser
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