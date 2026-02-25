package com.devtech.accahelps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devtech.accahelps.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior, viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = { Text("Question Picker") },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        actions = {
            val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
            if (isSyncing) Box(
                modifier = Modifier.padding(end = 8.dp).size(30.dp),
            ) {
                CircularProgressIndicator(

                )
            }
            else IconButton(
                onClick = viewModel::onRequestSync
            ) {
                Icon(Icons.Default.Sync, "Sync questions")
            }
        }
    )
}