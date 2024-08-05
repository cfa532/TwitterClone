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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.network.HproseInstance.appUser
import com.example.twitterclone.network.HproseInstance.getUrl
import com.example.twitterclone.ui.compose.AppIcon
import com.example.twitterclone.ui.compose.ComposeTweetScreen
import com.example.twitterclone.ui.feed.TweetFeedScreen
import com.example.twitterclone.ui.profile.PreferencesScreen
import com.example.twitterclone.ui.theme.TwitterCloneTheme
import com.example.twitterclone.viewmodel.TweetFeedViewModel

const val CURRENT_USER_ID = "5lrADJpzRpYZ82-6jkewoa1w3jB"

class MainActivity : ComponentActivity() {
    companion object {
        init {
            // init global data here
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(viewModel: TweetFeedViewModel = TweetFeedViewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val preferencesHelper = remember { PreferencesHelper(context) }

    Column {
        NavHost(
            navController = navController,
            startDestination = "tweetFeed",
        ) {
            composable("tweetFeed") {
                TweetFeedScreen(navController, viewModel)
            }
            composable("composeTweet") {
                ComposeTweetScreen(
                    navController = navController,
                    viewModel = viewModel,
                    currentUserMid = HproseInstance.appMid // Replace with actual current user ID
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
                AsyncImage(
                    model = if (appUser.avatar != null) { getUrl(appUser.avatar!!) } else { R.drawable.ic_user_avatar },
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
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* Navigate to Notice */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notice),
                    contentDescription = "Notice",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { navController.navigate("composeTweet") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_compose),
                    contentDescription = "Compose",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
