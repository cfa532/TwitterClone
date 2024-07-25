package com.example.twitterclone.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {

    private val _errorState = MutableLiveData<String?>(null)
    val errorState: LiveData<String?> = _errorState

    fun likeTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(likeCount = tweet.likeCount + 1)
            tweetRepository.updateTweet(updatedTweet)
        }
    }

    fun retweet(tweetMid: MimeiId, authorMid: MimeiId) {
        viewModelScope.launch {
            val originalTweet = tweetRepository.getTweet(tweetMid)
            val retweet = Tweet(
                content = originalTweet.content,
                timestamp = System.currentTimeMillis(),
                author = authorMid,
                original = originalTweet.mid
            )
            tweetRepository.addTweet(retweet)   // update tweet with Id returned from server
        }
    }

    fun uploadTweet(currentUserMid: MimeiId, content: String, isPrivate: Boolean, attachments: List<MimeiId>) {

        var tweet = Tweet(
            author = currentUserMid,
            content = content,
            isPrivate = isPrivate,
            attachments = attachments
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Perform long-running task here (e.g., network request, file I/O)
                try {
                    tweet = HproseInstance.uploadTweet(tweet)
                } catch (e: Exception) {
                    _errorState.value = e.message ?: "Failed to upload tweet"
                }
            }
        }

        // Now you can save the tweet with attachment CIDs
        println(tweet)
    }
}