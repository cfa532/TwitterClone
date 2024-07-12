package com.example.twitterclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.repository.TweetRepository
import com.example.twitterclone.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TweetFeedViewModel(
    private val tweetRepository: TweetRepository = TweetRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val _tweets = MutableStateFlow<List<us.leither.mytweet.model.Tweet>>(emptyList())
    val tweets: StateFlow<List<us.leither.mytweet.model.Tweet>> get() = _tweets

    init {
        viewModelScope.launch {
            _tweets.value = tweetRepository.getTweets()
        }
    }

    fun getUser(userMid: MimeiId): us.leither.mytweet.model.User {
        return userRepository.getUser(userMid)
    }

    fun getTweet(tweetMid: MimeiId): us.leither.mytweet.model.Tweet {
        return tweetRepository.getTweet(tweetMid)
    }
}