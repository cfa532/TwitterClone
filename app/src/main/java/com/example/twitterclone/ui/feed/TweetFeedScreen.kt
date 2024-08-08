package com.example.twitterclone.ui.feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.twitterclone.BottomNavigationBar
import com.example.twitterclone.MainTopAppBar
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.ui.tweet.TweetItem
import com.example.twitterclone.viewmodel.TweetFeedViewModel
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun TweetFeedScreen(navController: NavHostController, viewModel: TweetFeedViewModel) {
    Scaffold(
        topBar = { MainTopAppBar(navController) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        val tweets = viewModel.tweets.collectAsState().value
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
        {
            items(tweets) { tweet ->
                TweetItem(tweet)
            }
        }
    }
}
