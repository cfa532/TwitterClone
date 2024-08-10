package com.example.twitterclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.HproseInstance.appUser
import com.example.twitterclone.model.HproseInstance.uploadTweet
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.User
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {

    private val _tweet = MutableStateFlow<Tweet?>(null)
    val tweet: StateFlow<Tweet?> get() = _tweet.asStateFlow()

    private val _author = MutableStateFlow<User?>(null)
    val author: StateFlow<User?> get() = _author.asStateFlow()

    fun retweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.Default) {
            val retweet = Tweet(
                content = "",
                timestamp = System.currentTimeMillis(),
                authorId = appUser.mid,
                original = tweet.mid
            )
//            uploadTweet(retweet)
            _tweet.value = HproseInstance.retweetCount(tweet)
        }
    }

    fun likeTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _tweet.value = HproseInstance.likeTweet(tweet)
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                e.printStackTrace()
            }
        }
    }

    fun bookmarkTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _tweet.value = HproseInstance.bookmarkTweet(tweet)
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                e.printStackTrace()
            }
        }
    }

    fun setTweetAuthor() {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                _tweet.value?.let { HproseInstance.getUserPreview(it.authorId) }
            }
            _author.value = user
        }
    }

    fun setTweet(tweet: Tweet) {
        _tweet.value = tweet
    }
}