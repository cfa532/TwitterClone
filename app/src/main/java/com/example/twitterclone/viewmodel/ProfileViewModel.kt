package com.example.twitterclone.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    var username = mutableStateOf("")
    var name = mutableStateOf("")
    var profile = mutableStateOf("")

    init {
        // fetch data
    }
}