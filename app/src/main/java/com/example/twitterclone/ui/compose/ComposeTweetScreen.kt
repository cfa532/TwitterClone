package com.example.twitterclone.ui.compose

import AttachmentIcon
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.viewmodel.TweetViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ComposeTweetScreen(viewModel: TweetViewModel, currentUserMid: MimeiId) {
    var tweetContent by remember {mutableStateOf("") }
    val selectedAttachments =
        remember { mutableStateListOf<Uri>() } // Renamed for clarity
    var isPrivate by remember { mutableStateOf(false) }

    // Create a launcher for the file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedAttachments.add(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = tweetContent,
            onValueChange = { tweetContent = it },
            label = { Text("What's happening?") },
            modifier = Modifier.fillMaxWidth())
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
            horizontalArrangement = Arrangement.End // Align buttons to the right
        ) {
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Upload File")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.composeTweet(currentUserMid, tweetContent, isPrivate, selectedAttachments)
                    selectedAttachments.clear()
                    tweetContent = ""
                    // Consider navigating back to the previous screen or showing a confirmation message
                },
                modifier =Modifier.weight(1f)
            ) {
                Text("Tweet")
            }
        }

        // Display icons for attached files (moved outside of the button row)
        val groupedAttachments = selectedAttachments.chunked(2)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Control spacing between rows
        ) {
            items(groupedAttachments) { pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { uri ->
                        AttachmentIcon(uri)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun PreviewComposeTweetScreen() {
    // Create a dummy TweetViewModel and currentUserMid for preview purposes
    val dummyViewModel = TweetViewModel(/* Pass necessary parameters if any */)
    val dummyCurrentUserMid: MimeiId = "dummyUserId"

    ComposeTweetScreen(viewModel = dummyViewModel, currentUserMid = dummyCurrentUserMid)
}