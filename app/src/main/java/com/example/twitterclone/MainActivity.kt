package com.example.twitterclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.twitterclone.ui.theme.TwitterCloneTheme
import com.example.twitterclone.ui.tweet.TweetItem
import com.example.twitterclone.viewmodel.TweetFeedViewModel
import com.example.twitterclone.viewmodel.TweetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwitterCloneTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TweetFeedViewModel = TweetFeedViewModel()) {
    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        TweetFeed(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = "App Icon"
                )
            }
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_user_avatar),
                contentDescription = "User Avatar",
                modifier = Modifier.padding(8.dp)
            )
        }
    )
}

@Composable
fun BottomNavigationBar() {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Navigate to Home */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home")
            }
            IconButton(onClick = { /* Navigate to Notice */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_notice), contentDescription = "Notice")
            }
            IconButton(onClick = { /* Navigate to Compose */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_compose), contentDescription = "Compose")
            }
        }
    }
}

@Composable
fun TweetFeed(modifier: Modifier = Modifier, viewModel: TweetFeedViewModel) {
    val tweets = viewModel.tweets.collectAsState().value
    Column(modifier = modifier.fillMaxSize()) {
        tweets.forEach { tweet ->
            val author = viewModel.getUser(tweet.author)
            val tweetViewModel = TweetViewModel(viewModel.getTweetRepository())
            TweetItem(
                tweet = tweet,
                author = author,
                originalTweet = tweet.original?.let { viewModel.getTweet(it) },
                viewModel = tweetViewModel,
                currentUserMid = "currentUserId" // Replace with actual current user ID
            )
        }
    }
}