package com.example.witube.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.witube.presentation.viewmodel.AudioState
import com.example.witube.presentation.viewmodel.MainViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush

@Composable
fun WitubeApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        NavHost(navController = navController, startDestination = "first_screen") {
            composable("first_screen") {
                val audioState by viewModel.audioState.collectAsState()

                LaunchedEffect(audioState) {
                    if (audioState is AudioState.Success) {
                        navController.navigate("second_screen") {
                            // popUpTo("first_screen")
                        }
                    }
                }

                FirstScreen(
                    audioState = audioState,
                    onConvertClick = { url -> viewModel.fetchAudioInfo(url) },
                    onErrorDismiss = { viewModel.resetAudioState() }
                )
            }
            composable("second_screen") {
                val audioState by viewModel.audioState.collectAsState()
                val downloadState by viewModel.downloadState.collectAsState()

                androidx.activity.compose.BackHandler {
                    viewModel.resetAudioState()
                    viewModel.resetDownloadState()
                    navController.popBackStack()
                }

                if (audioState is AudioState.Success) {
                    val successState = audioState as AudioState.Success
                    SecondScreen(
                        info = successState.info,
                        url = successState.url,
                        downloadState = downloadState,
                        onDownloadClick = { url, fileName, uri ->
                            viewModel.downloadAudio(url, fileName, uri)
                        },
                        onBackClick = {
                            viewModel.resetAudioState()
                            viewModel.resetDownloadState()
                            navController.popBackStack()
                        },
                        onErrorDismiss = { viewModel.resetDownloadState() }
                    )
                }
            }
        }
    }
}
