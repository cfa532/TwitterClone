package com.example.twitterclone.model

import kotlinx.serialization.Serializable
typealias MimeiId = String      // 27 or 64 character long string

@Serializable
data class Tweet(
    var mid: MimeiId? = null,   // mid of the tweet
    val author: MimeiId,        // mid of the author
    var content: String,
    val timestamp: Long = System.currentTimeMillis(),
    var likeCount: Int = 0,     // Number of likes
    var bookmarkCount: Int = 0, // Number of bookmarks
    val original: MimeiId?= null, // this is retweet of the original tweet

    // List of retweets ID, without comments.
    var retweets: List<MimeiId> = emptyList(),
    var retweetCount: Int = 0,  // Number of retweets

    // List of comments (tweets) Id on this tweet.
    var comments: List<MimeiId> = emptyList(),
    var commentCount: Int = 0,  // Number of comments

    // List of media IDs attached to the tweet. Max 4 items for now.
    var attachments: List<MimeiId> = emptyList(),

    var isPrivate: Boolean = false,     // Viewable by the author only if true.
)

@Serializable
data class User(
    var mid: MimeiId, // Unique identifier for the user
    var name: String? = null,
    var username: String? = null,
    var avatar: MimeiId? = null, // Optional profile image URL

    // List of tweet MIDs bookmarked by the user
    var bookmarkedTweets: List<MimeiId> = emptyList(),

    // List of tweet MIDs liked by the user
    var likedTweets: List<MimeiId> = emptyList(),

    // List of tweet MIDs commented to by the user
    var repliedTweets: List<MimeiId> = emptyList(),

    // List of nodes authorized to the user to write tweets on.
    var nodeIds: List<MimeiId>? = null,

    var publicKey: String? = null,
)