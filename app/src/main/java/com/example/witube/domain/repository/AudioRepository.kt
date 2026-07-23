package com.example.witube.domain.repository

import android.net.Uri
import com.example.witube.domain.model.AudioInfo

interface AudioRepository {
    suspend fun getAudioInfo(ytUrl: String): AudioInfo
    suspend fun downloadAudio(ytUrl: String, fileName: String): Uri
    suspend fun downloadAudioToFolder(ytUrl: String, fileName: String, folderUri: Uri): Uri
}
