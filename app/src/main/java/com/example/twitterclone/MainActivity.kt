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
import androidx.compose.foundation.layout.size
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.twitterclone.ui.compose.ComposeTweetScreen

const val CURRENT_USER_ID = "5lrADJpzRpYZ82-6jkewoa1w3jB"

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
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tweetFeed",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tweetFeed") {
                TweetFeed(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
            }
            composable("composeTweet") {
                ComposeTweetScreen(
                    viewModel = TweetViewModel(viewModel.getTweetRepository()),
                    currentUserMid = CURRENT_USER_ID // Replace with actual current user ID
                )
            }
        }
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
                    contentDescription = "App Icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_user_avatar),
                contentDescription = "User Avatar",
                modifier = Modifier.padding(8.dp)
                    .size(40.dp)
                    .padding(top = 16.dp)
            )
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Navigate to Home */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = { /* Navigate to Notice */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notice),
                    contentDescription = "Notice",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = { navController.navigate("composeTweet") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_compose),
                    contentDescription = "Compose",
                    modifier = Modifier.size(24.dp)
                )
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