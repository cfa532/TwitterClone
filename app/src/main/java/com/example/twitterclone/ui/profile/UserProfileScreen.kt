package com.example.twitterclone.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.twitterclone.R
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.HproseInstance.appUser
import com.example.twitterclone.model.User
import com.example.twitterclone.ui.compose.ProfileTopAppBar

@Composable
fun UserProfileScreen(
    user: User,
    navController: NavHostController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(start = 16.dp, end = 16.dp)
    ) {
        // User header
        ProfileTopAppBar(navController)
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(Color.LightGray)
                .padding(start = 16.dp, end = 16.dp, top = 3.dp, bottom = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Image(
                painter = rememberAsyncImagePainter(appUser.baseUrl?.let { HproseInstance.getMediaUrl(
                    user.avatar, it) }),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.width(IntrinsicSize.Min)) {
                    Text("Edit")
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = user.name ?: "No one",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = "@${user.username}" ?: "@NoOne")
            Text(text = user.profile ?: "Profile") // Replace with actual resume
        }
        // Add more user details here, like following/followers count

    }
}
