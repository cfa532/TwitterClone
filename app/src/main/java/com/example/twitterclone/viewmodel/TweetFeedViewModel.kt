package com.example.twitterclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.User
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.repository.TweetRepository
import com.example.twitterclone.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TweetFeedViewModel(
    private val tweetRepository: TweetRepository = TweetRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _tweets = MutableStateFlow<List<Tweet>>(emptyList())
    val tweets: StateFlow<List<Tweet>> get() = _tweets

    init {
        viewModelScope.launch {
//            _tweets.value = tweetRepository.getTweets()
            _tweets.value = HproseInstance.getTweets()
            println("tweets: ${_tweets.value}")
        }
    }

    fun getUser(userMid: MimeiId): User {
        return userRepository.getUser(userMid)
    }

    fun getTweet(tweetMid: MimeiId): Tweet {
        return tweetRepository.getTweet(tweetMid)
    }

    fun getTweetRepository(): TweetRepository {
        return tweetRepository
    }
}