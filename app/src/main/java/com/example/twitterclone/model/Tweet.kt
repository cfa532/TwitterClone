package com.example.twitterclone.model

import kotlinx.serialization.Serializable
typealias MimeiId = String      // 27 or 64 character long string

@Serializable
data class Tweet(
    var mid: MimeiId? = null,   // mid of the tweet
    val authorId: MimeiId,        // mid of the author, is also the mimei database Id
    var content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val original: MimeiId?= null, // this is retweet of the original tweet

    var likeCount: Int = 0,     // Number of likes
    var likes: List<MimeiId> = emptyList(),     // user list that liked the tweet

    var bookmarkCount: Int = 0, // Number of bookmarks
    var bookmarks: List<MimeiId> = emptyList(), // user list that bookmarked the tweet

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
    var mid: MimeiId, // Unique identifier for the user, and the mimei database
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