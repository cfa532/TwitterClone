package com.example.twitterclone

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.ui.compose.AppIcon
import com.example.twitterclone.ui.compose.ComposeTweetScreen
import com.example.twitterclone.ui.profile.PreferencesScreen

const val CURRENT_USER_ID = "5lrADJpzRpYZ82-6jkewoa1w3jB"

class MainActivity : ComponentActivity() {
    companion object {
        init {
            HproseInstance.initialize()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwitterCloneTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(viewModel: TweetFeedViewModel = TweetFeedViewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val preferencesHelper = remember { PreferencesHelper(context) }

    Scaffold(
//        topBar = { MainTopAppBar(navController) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tweetFeed",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tweetFeed") {
                TweetFeedScreen(navController, viewModel)
            }
            composable("composeTweet") {
                ComposeTweetScreen(
                    viewModel = TweetViewModel(viewModel.getTweetRepository()),
                    currentUserMid = CURRENT_USER_ID // Replace with actual current user ID
                )
            }
            composable("preferences") {
                PreferencesScreen(navController, preferencesHelper)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AppIcon()
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("preferences") }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_avatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweetFeedScreen(navController: NavHostController, viewModel: TweetFeedViewModel) {
    Scaffold(
        topBar = { MainTopAppBar(navController) }
    ) { innerPadding ->
        TweetFeed(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@Composable
fun TweetFeed(modifier: Modifier = Modifier, viewModel: TweetFeedViewModel) {
    val tweets = viewModel.tweets.collectAsState().value
    Column(modifier = modifier.fillMaxSize()) {
        tweets.forEach { tweet ->
            TweetItem( tweet = tweet )
        }
    }
}