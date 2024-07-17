package com.example.twitterclone.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun ComposeTweetScreen(viewModel: TweetViewModel, currentUserMid: MimeiId) {
    var tweetContent by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = tweetContent,
            onValueChange = { tweetContent = it },
            label = { Text("What's happening?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Private")
            Switch(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.composeTweet(
                    authorId = currentUserMid,
                    content = tweetContent,
                    isPrivate = isPrivate
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Tweet")
        }
    }
}