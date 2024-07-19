package com.example.twitterclone.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.viewmodel.TweetViewModel

@Composable
fun ComposeTweetScreen(viewModel: TweetViewModel, currentUserMid: MimeiId) {
    val tweetContent = remember { mutableStateOf("") }
    val attachments = remember { mutableStateOf(listOf<MimeiId>()) }
    var isPrivate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = tweetContent.value,
            onValueChange = { tweetContent.value = it },
            label = { Text("What's happening?") }
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
        Button(onClick = {
            viewModel.composeTweet(currentUserMid, tweetContent.value, isPrivate)
        },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Tweet")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComposeTweetScreen() {
    // Create a dummy TweetViewModel and currentUserMid for preview purposes
    val dummyViewModel = TweetViewModel(/* Pass necessary parameters if any */)
    val dummyCurrentUserMid: MimeiId = "dummyUserId"

    ComposeTweetScreen(viewModel = dummyViewModel, currentUserMid = dummyCurrentUserMid)
}