package com.example.twitterclone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.HproseInstance
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
    val tweet: StateFlow<Tweet?> = _tweet.asStateFlow()

    private var _author = MutableLiveData<User>()
    val author: LiveData<User> get() = _author

    fun likeTweet() {
        viewModelScope.launch {
            val currentTweet = _tweet.value
            if (currentTweet != null) {
                val likedTweet = withContext(Dispatchers.IO) {
                    HproseInstance.likeTweet(currentTweet)
                }
                _tweet.value = likedTweet
            }
        }
    }

    fun getAuthor(authorId: MimeiId) {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                HproseInstance.getUserPreview(authorId)
            }
            user.let { _author.value = it }
        }
    }

    fun setTweet(tweet: Tweet) {
        _tweet.value = tweet
    }

    fun retweet(tweetMid: MimeiId, authorMid: MimeiId) {
        viewModelScope.launch {
            val originalTweet = tweetRepository.getTweet(tweetMid)
            val retweet = Tweet(
                content = originalTweet.content,
                timestamp = System.currentTimeMillis(),
                authorId = authorMid,
                original = originalTweet.mid
            )
            tweetRepository.addTweet(retweet)   // update tweet with Id returned from server
        }
    }
}