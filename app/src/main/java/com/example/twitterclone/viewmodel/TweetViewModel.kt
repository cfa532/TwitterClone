package com.example.twitterclone.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.launch

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {
    private val _attachments = MutableLiveData<List<Uri>>()
    val attachments: LiveData<List<Uri>> get() = _attachments

    fun likeTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(likeCount = tweet.likeCount + 1)
            tweetRepository.updateTweet(updatedTweet)
        }
    }

    fun addAttachment(uri: Uri) {
        _attachments.value = (_attachments.value ?: emptyList()) + uri
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

    fun composeTweet(currentUserMid: MimeiId, content: String, isPrivate: Boolean, attachments: List<Uri>) {
        // Handle tweet composition, including attachments
        val tweet = Tweet(
            author = currentUserMid,
            content = content,
            isPrivate = isPrivate,
            attachments = attachments.map { it.toString() }
//            attachments = _attachments.value?.map { it.toString() } ?: emptyList()
        )
        println(tweet)
        tweetRepository.addTweet(tweet)
        // Add logic to save or upload the tweet
    }
}