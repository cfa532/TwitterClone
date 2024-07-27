package com.example.twitterclone.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.twitterclone.PreferencesHelper
import com.example.twitterclone.R
import com.example.twitterclone.network.HproseInstance
import com.example.twitterclone.ui.compose.AppIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(navController: NavHostController, preferencesHelper: PreferencesHelper) {
    var username by remember { mutableStateOf(preferencesHelper.getUsername()?.takeIf { it.isNotEmpty() } ?: "NoOne") }
    var name by remember { mutableStateOf(preferencesHelper.getName() ?: "No One") }
    var entryUrl by remember { mutableStateOf(preferencesHelper.getEntryUrl() ?: "tw.fireshare.us") }

    val user = HproseInstance.getUserData()
    if (user != null) {
        username = user.username ?: username
        name = user.name ?: name
    }

    Column(modifier = Modifier.padding(16.dp)) {
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
                        modifier = Modifier.size(40.dp)
                            .padding(8.dp)
                    )
                }
            }
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = entryUrl,
            onValueChange = { entryUrl = it },
            label = { Text("Entry URL") }
        )
        Button(onClick = {
            preferencesHelper.saveUsername(username)
            preferencesHelper.saveName(name)
            preferencesHelper.saveEntryUrl(entryUrl)

            if (user != null) {
                user.username = username
                user.name = name
                HproseInstance.setUserData(user)
            }
        }) {
            Text("Save")
        }
    }
}
