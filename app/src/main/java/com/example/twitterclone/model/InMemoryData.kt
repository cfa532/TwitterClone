package com.example.twitterclone.model

import kotlinx.coroutines.flow.MutableStateFlow

//data class Tweet(
//    var mid: MimeiId? = null,   // mid of the tweet
//    val authorId: MimeiId,        // mid of the author, is also the mimei database Id
//    var content: String,
//    val timestamp: Long = System.currentTimeMillis(),   // timestamp tweet is read into memory
//
//    var likeCount: Int = 0,     // Number of likes
//    var bookmarkCount: Int = 0, // Number of bookmarks
//    var retweetCount: Int = 0,  // Number of retweets
//    var commentCount: Int = 0,  // Number of comments
//    var attachments: List<MimeiId>? = emptyList(),
//    var isPrivate: Boolean = false,     // Viewable by the author only if true.
//
//    val original: Tweet? = null, // this is retweet of the original tweet
//
//    // if the current user has liked or bookmarked this tweet
//    var hasLiked: Boolean = false,
//    var hasBookmarked: Boolean = false,
//)

object InMemoryData {
    var users: MutableList<User> = emptyList<User>().toMutableList()
    var tweets: MutableStateFlow<List<Tweet>> = MutableStateFlow(emptyList())
}
