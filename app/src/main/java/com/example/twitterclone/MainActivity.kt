package com.example.twitterclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.twitterclone.repository.HproseInstance.appUser
import com.example.twitterclone.repository.HproseInstance.getMediaUrl
import com.example.twitterclone.ui.compose.AppIcon
import com.example.twitterclone.ui.compose.ComposeTweetScreen
import com.example.twitterclone.ui.feed.TweetFeedScreen
import com.example.twitterclone.ui.profile.PreferencesScreen
import com.example.twitterclone.ui.profile.UserProfileScreen
import com.example.twitterclone.ui.theme.TwitterCloneTheme
import com.example.twitterclone.viewmodel.TweetFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

const val CURRENT_USER_ID = "5lrADJpzRpYZ82-6jkewoa1w3jB"
var httpClient: OkHttpClient = OkHttpClient()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        init {
            // init global data here
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }

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
                )
            }
            composable("userProfile") {
                UserProfileScreen(appUser, navController, viewModel )
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
            IconButton(onClick = { navController.navigate("userProfile") }) {
                appUser.baseUrl?.let { getMediaUrl(appUser.avatar, it) }?.let {
                    Image(
                        painter = rememberAsyncImagePainter(appUser.baseUrl?.let { getMediaUrl(
                            appUser.avatar, it) }),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
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
            IconButton(onClick = { navController.navigate("tweetFeed") }) {
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
