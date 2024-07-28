package com.example.twitterclone.viewmodel

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
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _tweets = MutableStateFlow<List<Tweet>>(emptyList())
    val tweets: StateFlow<List<Tweet>> get() = _tweets

    init {
        getTweets()
    }

    fun getUser(userMid: MimeiId): User {
        return userRepository.getUser(userMid)
    }

    private fun getTweets(
        startTimestamp: Long = System.currentTimeMillis(),
        endTimestamp: Long? = startTimestamp - 72 * 60 * 60 * 1000
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

    fun getTweetRepository(): TweetRepository {
        return tweetRepository
    }
}