package com.example.twitterclone.model

data class Tweet(
    val mid: MimeiId,
    val content: String,
    val timestamp: Long,
    val author: MimeiId,        // mid of the author
    val likeCount: Int = 0,     // Number of likes
    val bookmarkCount: Int = 0, // Number of bookmarks
    var original: MimeiId?= null, // this is retweet of the original tweet

    // List of retweets ID, without comments.
    val retweets: List<MimeiId> = emptyList(),
    val retweetCount: Int = 0,  // Number of retweets

    // List of comments (tweets) Id on this tweet.
    val comments: List<MimeiId> = emptyList(),
    val commentCount: Int = 0,  // Number of comments

    // List of media IDs attached to the tweet. Max 4 items for now.
    val mediaIds: List<MimeiId> = emptyList(),
)