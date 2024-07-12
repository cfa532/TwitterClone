package com.example.twitterclone.model

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
    val repliedTweets: List<MimeiId> = emptyList()
)
