package com.example.witube.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.witube.domain.model.AudioInfo
import com.example.witube.domain.usecase.DownloadAudioUseCase
import com.example.witube.domain.usecase.GetAudioInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AudioState {
    object Idle : AudioState()
    object Loading : AudioState()
    data class Success(val info: AudioInfo, val url: String) : AudioState()
    data class Error(val message: String) : AudioState()
}

sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    data class Success(val uri: Uri, val fileName: String) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

class MainViewModel(
    private val getAudioInfoUseCase: GetAudioInfoUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase
) : ViewModel() {

    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    fun fetchAudioInfo(url: String) {
        if (url.isBlank()) {
            _audioState.value = AudioState.Error("La URL no puede estar vacía")
            return
        }
        _audioState.value = AudioState.Loading
        viewModelScope.launch {
            try {
                val info = getAudioInfoUseCase(url)
                _audioState.value = AudioState.Success(info, url)
            } catch (e: Exception) {
                _audioState.value = AudioState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetAudioState() {
        _audioState.value = AudioState.Idle
    }

    fun downloadAudio(url: String, fileName: String, folderUri: Uri? = null) {
        _downloadState.value = DownloadState.Downloading
        viewModelScope.launch {
            try {
                val uri = downloadAudioUseCase(url, fileName, folderUri)
                _downloadState.value = DownloadState.Success(uri, fileName)
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Error("Error al descargar: ${e.message}")
            }
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
}
