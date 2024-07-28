package com.example.twitterclone.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.User
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.repository.TweetRepository
import com.example.twitterclone.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TweetFeedViewModel(
    private val tweetRepository: TweetRepository = TweetRepository(),
) : ViewModel() {

    private var startTimestamp = mutableLongStateOf(System.currentTimeMillis())     // current time
    private var endTimestamp = mutableLongStateOf(System.currentTimeMillis() - 1000 * 60 * 60 * 72)     // previous time
    private val _tweets = MutableStateFlow<List<Tweet>>(emptyList())
    val tweets: StateFlow<List<Tweet>> get() = _tweets

    init {
        getTweets(startTimestamp.longValue, endTimestamp.longValue)
    }

    private fun getTweets(
        startTimestamp: Long = System.currentTimeMillis(),
        endTimestamp: Long? = null
    ) {
        viewModelScope.launch {
            val followings = HproseInstance.getFollowings()
            followings.forEach { userId ->
                async {
                    val tweetsList = _tweets.value.filter { it.authorId == userId }.toMutableList()
                    HproseInstance.getTweets(userId, tweetsList, startTimestamp, endTimestamp)
                    tweetsList
                }.await().also { newTweets ->
                    _tweets.update { currentTweets -> // Use update to ensure thread safety
                        currentTweets + newTweets
                    }
                }
            }
        }
    }

    fun uploadTweet(currentUserMid: MimeiId, content: String, isPrivate: Boolean, attachments: List<MimeiId>) {
        var tweet = Tweet(
            authorId = currentUserMid,
            content = content,
            isPrivate = isPrivate,
            attachments = attachments
        )
        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
            try {
                tweet = HproseInstance.uploadTweet(tweet)
            } catch (e: Exception) {
                Log.e("TweetFeedViewModel", "Error uploading tweet", e)
            }
//            }
        }
        println(tweet)
        _tweets.update { currentTweets ->
            currentTweets + tweet
        }
    }
}