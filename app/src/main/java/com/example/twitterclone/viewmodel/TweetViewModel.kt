package com.example.twitterclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.HproseInstance.appUser
import com.example.twitterclone.model.HproseInstance.toggleRetweet
import com.example.twitterclone.model.InMemoryData
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {

    private val _tweet = MutableStateFlow<Tweet?>(null)
    val tweet: StateFlow<Tweet?> get() = _tweet.asStateFlow()

    fun retweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.Default) {
            val retweet = if (tweet.content == "" && tweet.originalTweet != null) {
                // the tweet to be forwarded is a retweet itself. Find the original tweet to forward.
                Tweet(
                    content = "",
                    timestamp = System.currentTimeMillis(),
                    authorId = appUser.mid,
                    originalTweetId = tweet.originalTweetId,
                    originalAuthorId = tweet.originalAuthorId
                )
            } else {
                Tweet(
                    content = "",
                    timestamp = System.currentTimeMillis(),
                    authorId = appUser.mid,
                    originalTweetId = tweet.mid,
                    originalAuthorId = tweet.authorId)
            }
            toggleRetweet(retweet)?.let {
                _tweet.value = if (it.mid != null) {
                    tweet.copy(retweetCount = _tweet.value?.retweetCount?.plus(1) ?: 0)
                } else {
                    tweet.copy(retweetCount = _tweet.value?.retweetCount?.minus(1) ?: 0)
                }
            }
        }
    }

    fun likeTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _tweet.value = HproseInstance.likeTweet(tweet) ?: _tweet.value
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun bookmarkTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _tweet.value = HproseInstance.bookmarkTweet(tweet) ?: _tweet.value
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setTweet(tweet: Tweet) {
        _tweet.value = tweet
    }
}