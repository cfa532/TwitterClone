package com.example.twitterclone.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.network.TweetRequest
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.launch

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {

    private val tweets = mutableStateListOf<Tweet>()

    fun likeTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(likeCount = tweet.likeCount + 1)
            tweetRepository.updateTweet(updatedTweet)
        }
    }

    fun retweet(tweetMid: MimeiId, authorMid: MimeiId) {
        viewModelScope.launch {
            val originalTweet = tweetRepository.getTweet(tweetMid)
            val retweet = Tweet(
                content = originalTweet.content,
                timestamp = System.currentTimeMillis(),
                author = authorMid,
                original = originalTweet.mid
            )
            tweetRepository.addTweet(retweet)   // update tweet with Id returned from server
        }
    }

    fun composeTweet(authorId: MimeiId, content: String, isPrivate: Boolean) {
        viewModelScope.launch {
            val newTweet = Tweet(author = authorId, content = content, isPrivate = isPrivate)
            println(newTweet)
            val tweetRequest = TweetRequest(newTweet)

//            val (result, error) = HproseInstance.client.GetVar("", "ver")
            val error = null
            val result = HproseInstance.client.GetVarByContext("", "context_ppt")
            if (error == null) {
                println("Version: $result")
            } else {
                println(error)
            }
        }
    }
}