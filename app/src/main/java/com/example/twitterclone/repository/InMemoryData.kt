package com.example.twitterclone.repository

import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.User
import kotlinx.coroutines.flow.MutableStateFlow

object InMemoryData {
    var users: MutableSet<User> = emptySet<User>().toMutableSet()
    var _tweets: MutableStateFlow<List<Tweet>> = MutableStateFlow(emptyList())
}
