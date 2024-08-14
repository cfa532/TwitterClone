package com.example.twitterclone.ui.profile

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.twitterclone.PreferencesHelper
import com.example.twitterclone.R
import com.example.twitterclone.model.HproseInstance
import com.example.twitterclone.model.HproseInstance.appUser
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User
import com.example.twitterclone.ui.compose.AppIcon
import com.example.twitterclone.ui.compose.CircularImage
import com.example.twitterclone.viewmodel.TweetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PreferencesScreen(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper,
) {
    var username by rememberSaveable { mutableStateOf(preferencesHelper.getUsername()?.takeIf { it.isNotEmpty() } ?: "NoOne") }
    var name by rememberSaveable { mutableStateOf(preferencesHelper.getName() ?: "No One") }
    var avatar by rememberSaveable { mutableStateOf<MimeiId?>(appUser.avatar) }
    val user by remember { mutableStateOf<User?>(appUser) }

//    appUser.avatar?.let { avatar = it }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(it)?.let { stream ->
                            val mimeiId = HproseInstance.uploadToIPFS(stream)
                            avatar = mimeiId
                            user?.let { user ->
                                user.avatar = mimeiId
                                HproseInstance.setUserData(user)
                            }
                        }
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PreferencesTopAppBar(navController)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Preferences", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(16.dp))
        AvatarSection(avatar, launcher)
        Spacer(modifier = Modifier.height(16.dp))
        PreferencesForm(
            username = username,
            name = name,
            onUsernameChange = { newUsername -> username = newUsername },
            onNameChange = { newName -> name = newName },
        )
        SaveButton(preferencesHelper, username, name, user, coroutineScope)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesTopAppBar(navController: NavHostController) {
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }
        }
    )
}

@Composable
fun AvatarSection(avatar: MimeiId?, launcher: ManagedActivityResultLauncher<String, Uri?>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .padding(0.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(appUser.baseUrl?.let { HproseInstance.getMediaUrl(
                    avatar, it) }),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .clip(CircleShape) // Clip the image to match the button's shape
            )
        }
    }
}

@Composable
fun PreferencesForm(
    username: String,
    name: String,
    onUsernameChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
) {
    Column {
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SaveButton(
    preferencesHelper: PreferencesHelper,
    username: String,
    name: String,
    user: User?,
    coroutineScope: CoroutineScope
) {
    Button(
        onClick = {
            preferencesHelper.saveUsername(username)
            preferencesHelper.saveName(name)

            user?.let {
                it.username = username
                it.name = name
                coroutineScope.launch(Dispatchers.Default) {
                    HproseInstance.setUserData(it)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .width(intrinsicSize = IntrinsicSize.Min)
    ) {
        Text("Save")
    }
}