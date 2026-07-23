package com.example.witube.domain.usecase

import com.example.witube.domain.model.AudioInfo
import com.example.witube.domain.repository.AudioRepository

class GetAudioInfoUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(ytUrl: String): AudioInfo {
        return repository.getAudioInfo(ytUrl)
    }
}
