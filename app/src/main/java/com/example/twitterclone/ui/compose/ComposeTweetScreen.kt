package com.example.twitterclone.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ComposeTweetScreen(
    viewModel: ComposeTweetViewModel = viewModel(),
    onTweetPosted:() -> Unit
) {
    var tweetContent by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = tweetContent,
            onValueChange = { tweetContent = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .background(Color.LightGray),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.postTweet(tweetContent.text)
                onTweetPosted()
            },
            enabled = tweetContent.text.isNotEmpty(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Tweet")
        }
    }

}