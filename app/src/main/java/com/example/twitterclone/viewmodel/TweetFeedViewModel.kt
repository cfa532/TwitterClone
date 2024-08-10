package com.example.twitterclone.viewmodel

import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TweetFeedViewModel(
    private val tweetRepository: TweetRepository = TweetRepository(),
) : ViewModel() {

    private val _tweets = MutableStateFlow<List<Tweet>>(emptyList())
    val tweets: StateFlow<List<Tweet>> get() = _tweets

    private var startTimestamp = mutableLongStateOf(System.currentTimeMillis())     // current time
    private var endTimestamp = mutableLongStateOf(System.currentTimeMillis() - 1000 * 60 * 60 * 72)     // previous time

    init {
//        getTweets(startTimestamp.longValue, endTimestamp.longValue)
        getTweets(startTimestamp.longValue)
    }

    private fun getTweets(
        startTimestamp: Long = System.currentTimeMillis(),
        endTimestamp: Long? = null
    ) {
        viewModelScope.launch {
            val followings = HproseInstance.getFollowings()
            followings.forEach { userId ->
                withContext(Dispatchers.IO) {
                    val tweetsList = _tweets.value.filter { it.authorId == userId }.toMutableList()
                    HproseInstance.getTweetList(userId, tweetsList, startTimestamp, endTimestamp)
                    tweetsList
                }.also { newTweets ->
                    _tweets.update { currentTweets -> // Use update to ensure thread safety
                        currentTweets + newTweets
                    }
                }
            }
        }
    }

    fun uploadTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.IO) {
            HproseInstance.uploadTweet(tweet, false)?.let { newTweet ->
                _tweets.update { currentTweets ->
                    // add new tweet at top of the list
                    listOf(newTweet) + currentTweets
                }
            }
        }
    }
}