package com.example.twitterclone.model

typealias MimeiId = String

data class Tweet(
    val mid: MimeiId,
    val content: String,
    val timestamp: Long,
    val author: MimeiId,        // mid of the author
    var likeCount: Int = 0,     // Number of likes
    var bookmarkCount: Int = 0, // Number of bookmarks
    var original: MimeiId?= null, // this is retweet of the original tweet

    // List of retweets ID, without comments.
    val retweets: List<MimeiId> = emptyList(),
    val retweetCount: Int = 0,  // Number of retweets

    // List of comments (tweets) Id on this tweet.
    val comments: List<MimeiId> = emptyList(),
    val commentCount: Int = 0,  // Number of comments

    // List of media IDs attached to the tweet. Max 4 items for now.
    val mediaIds: List<MimeiId> = emptyList(),

    var isPrivate: Boolean = false,     // Viewable by the author only if true.
)

data class User(
    val mid: MimeiId, // Unique identifier for the user
    val name: String,
    val username: String,
    val avatar: MimeiId? = null, // Optional profile image URL

    // List of tweet MIDs bookmarked by the user
    val bookmarkedTweets: List<MimeiId> = emptyList(),

    // List of tweet MIDs liked by the user
    val likedTweets: List<MimeiId> = emptyList(),

    // List of tweet MIDs commented to by the user
    val repliedTweets: List<MimeiId> = emptyList(),

    // List of nodes authorized to the user to write tweets on.
    val nodeIds: List<MimeiId>? = null,

    val publicKey: String? = null,

    // List of users blocked by the user
    val blockList: List<MimeiId>? = null
)