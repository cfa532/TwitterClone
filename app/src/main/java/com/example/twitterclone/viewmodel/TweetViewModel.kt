package com.example.twitterclone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.launch
import java.time.Instant

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {
    private val _tweet = MutableLiveData<Tweet>()
    val tweet: LiveData<Tweet> get() = _tweet

    fun likeTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(likeCount = tweet.likeCount + 1)
            tweetRepository.updateTweet(updatedTweet)
            _tweet.value = updatedTweet
        }
    }

    fun retweet(tweetMid: MimeiId, authorMid: MimeiId) {
        viewModelScope.launch {
            val originalTweet = tweetRepository.getTweet(tweetMid)
            val retweet = Tweet(
                mid = generateMimeiId(), // Function to generate a unique ID
                content = originalTweet.content,
                timestamp = System.currentTimeMillis(),
                author = authorMid,
                original = originalTweet.mid
            )
            tweetRepository.addTweet(retweet)
            _tweet.value = retweet
        }
    }

    private fun generateMimeiId(): MimeiId {
        // Implement a function to generate a unique ID for each tweet
        return java.util.UUID.randomUUID().toString()
    }

    private fun saveTweetToDatabase(tweet: Tweet) {
        // Implement your proprietary database access logic here
    }

    fun composeTweet(authorId: MimeiId, content: String, mediaIds: List<MimeiId> = emptyList(), isPrivate: Boolean = false) {
        val tweetId = generateMimeiId()
        val timestamp = Instant.now().toEpochMilli()
        val newTweet = Tweet(
            mid = tweetId,
            content = content,
            timestamp = timestamp,
            author = authorId,
            mediaIds = mediaIds,
            isPrivate = isPrivate
        )
        saveTweetToDatabase(newTweet)
        _tweet.value = newTweet
    }
}