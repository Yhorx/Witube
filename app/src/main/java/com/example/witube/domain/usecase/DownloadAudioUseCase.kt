package com.example.witube.domain.usecase

import android.net.Uri
import com.example.witube.domain.repository.AudioRepository

class DownloadAudioUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(ytUrl: String, fileName: String, folderUri: Uri? = null): Uri {
        return if (folderUri != null) {
            repository.downloadAudioToFolder(ytUrl, fileName, folderUri)
        } else {
            repository.downloadAudio(ytUrl, fileName)
        }
    }
}
