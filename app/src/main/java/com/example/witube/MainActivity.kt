package com.example.witube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.witube.data.remote.WitubeApi
import com.example.witube.data.repository.AudioRepositoryImpl
import com.example.witube.domain.usecase.DownloadAudioUseCase
import com.example.witube.domain.usecase.GetAudioInfoUseCase
import com.example.witube.presentation.theme.WitubeTheme
import com.example.witube.presentation.ui.WitubeApp
import com.example.witube.presentation.viewmodel.MainViewModel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // No action needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val api = WitubeApi()
        val repository = AudioRepositoryImpl(api, this)
        val getAudioInfoUseCase = GetAudioInfoUseCase(repository)
        val downloadAudioUseCase = DownloadAudioUseCase(repository)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(getAudioInfoUseCase, downloadAudioUseCase) as T
            }
        }

        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            WitubeTheme {
                WitubeApp(viewModel = viewModel)
            }
        }
    }
}
