package com.example.twitterclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.HproseInstance.appUser
import com.example.twitterclone.model.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TweetViewModel() : ViewModel()
{
    private val _tweet = MutableStateFlow<Tweet?>(null)
    val tweet: StateFlow<Tweet?> get() = _tweet.asStateFlow()

    fun toggleRetweet(tweet: Tweet) {
        var originalTweet: Tweet = tweet
        viewModelScope.launch(Dispatchers.Default) {
            tweet.originalTweet?.let {
                if (tweet.content == "") {
                    // the tweet to be forwarded is a retweet itself. Find the original tweet to forward.
                    originalTweet = it
                } else {
                    // update timestamp of the old retweet to move it forward.
                }
            }
            HproseInstance.toggleRetweet( originalTweet )?.let {
                _tweet.value = it
//                _tweet.value = if (it.mid != null) {
//                    tweet.copy(retweetCount = _tweet.value?.retweetCount?.plus(1) ?: 0)
//                } else {
//                    tweet.copy(retweetCount = _tweet.value?.retweetCount?.minus(1) ?: 0)
//                }
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