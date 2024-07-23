package com.example.twitterclone.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.viewmodel.TweetViewModel
import androidx.compose.runtime.mutableStateListOf

@Composable
fun ComposeTweetScreen(viewModel: TweetViewModel, currentUserMid: MimeiId) {
    var tweetContent by remember {mutableStateOf("") }
    val attachments = remember { mutableStateListOf<Uri>() } // Consider renaming to 'selectedAttachments' for clarity
    var isPrivate by remember { mutableStateOf(false) }

    // Create a launcher for the file picker
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addAttachment(it)
            attachments.add(it)
            // Consider updating 'selectedAttachments' state here if you want to display selected files
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = tweetContent,
            onValueChange = { tweetContent = it },label = { Text("What's happening?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Private")
            Switch(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Consider using Arrangement.End to align buttons to the right
        ) {
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.weight(1f) // Use weight to distribute space evenly
            ) {
                Text("Upload File")
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
            Button(
                onClick = {
                    viewModel.composeTweet(currentUserMid, tweetContent, isPrivate)
                    // Consider clearing 'tweetContent' and 'selectedAttachments' after posting
                    // and navigating back to the previous screen
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Tweet")
            }
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