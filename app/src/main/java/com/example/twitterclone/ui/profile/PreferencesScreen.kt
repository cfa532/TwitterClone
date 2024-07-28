package com.example.twitterclone.ui.profile

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.twitterclone.PreferencesHelper
import com.example.twitterclone.R
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.network.HproseInstance.uploadToIPFS
import com.example.twitterclone.ui.compose.AppIcon
import kotlinx.coroutines.launch
import com.example.twitterclone.model.MimeiId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(navController: NavHostController, preferencesHelper: PreferencesHelper) {
    var username by remember {
        mutableStateOf(
            preferencesHelper.getUsername()?.takeIf { it.isNotEmpty() } ?: "NoOne")
    }
    var name by remember { mutableStateOf(preferencesHelper.getName() ?: "No One") }
    var entryUrl by remember {
        mutableStateOf(
            preferencesHelper.getEntryUrl() ?: "tw.fireshare.us"
        )
    }
    var avatar by remember { mutableStateOf<MimeiId?>(null) }
    val user = HproseInstance.getUserData()
    if (user != null) {
        username = user.username ?: username
        name = user.name ?: name
        avatar = user.avatar
    }

    val context = LocalContext.current // Renamed for clarity
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val inputStream = context.contentResolver.openInputStream(it)
                inputStream?.let { stream ->
                    val mimeiId = uploadToIPFS(stream)
                    avatar = mimeiId
                    if (user != null) {
                        user.avatar = mimeiId
                        HproseInstance.setUserData(user)
                    }
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
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
        Column(
        ) {
            Text("Preferences")
            AvatarSection(avatar, launcher)
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                value = entryUrl,
                onValueChange = { entryUrl = it },
                label = { Text("Entry URL") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Button(
            onClick = {
                preferencesHelper.saveUsername(username)
                preferencesHelper.saveName(name)
                preferencesHelper.saveEntryUrl(entryUrl)

                if (user != null) {
                    user.username = username
                    user.name = name
                    HproseInstance.setUserData(user)
                }
            },
            Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun AvatarSection(avatar: String?, launcher: ManagedActivityResultLauncher<String, Uri?>) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = if (avatar != null) { HproseInstance.BASE_URL+"/ipfs/"+avatar } else { R.drawable.ic_user_avatar },
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .padding(8.dp)
        )
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Upload Avatar")
        }
    }
}