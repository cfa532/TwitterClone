package com.example.twitterclone.model

import kotlinx.coroutines.flow.MutableStateFlow

object InMemoryData {
    var users: MutableSet<User> = emptySet<User>().toMutableSet()
    var _tweets: MutableStateFlow<List<Tweet>> = MutableStateFlow(emptyList())

}
