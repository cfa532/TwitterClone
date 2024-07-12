package com.example.twitterclone.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.launch

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {

    fun bookmarkTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(bookmarkCount = tweet.bookmarkCount + 1)
            tweetRepository.updateTweet(updatedTweet)
        }
    }
}