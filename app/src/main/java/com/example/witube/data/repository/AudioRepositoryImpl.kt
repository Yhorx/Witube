package com.example.witube.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.witube.data.remote.WitubeApi
import com.example.witube.domain.model.AudioInfo
import com.example.witube.domain.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AudioRepositoryImpl(
    private val api: WitubeApi,
    private val context: Context
) : AudioRepository {

    override suspend fun getAudioInfo(ytUrl: String): AudioInfo = withContext(Dispatchers.IO) {
        val data = api.reqData(ytUrl, "info-audio").use { response ->
            if (!response.isSuccessful) {
                throw Exception("Respuesta del servidor: ${response.code}")
            }
            response.body?.string().orEmpty()
        }

        val json = JSONObject(data)
        val title = json.getString("title")
        val artist = json.getString("channel")
        val duration = json.getString("duration_string")
        val thumbnail = json.getString("thumbnail")

        AudioInfo(
            title = title,
            duration = duration,
            artists = artist,
            thumbnail = thumbnail
        )
    }

    override suspend fun downloadAudio(ytUrl: String, fileName: String): Uri = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val safeFileName = safeFileName(fileName)
            val outputUri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "$safeFileName.mp3")
                    put(MediaStore.Downloads.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Witube")
                }
            ) ?: throw Exception("No se pudo crear el archivo en Downloads")

            api.reqData(ytUrl, "download-audio").use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Respuesta del servidor: ${response.code}")
                }
                val body = response.body ?: throw Exception("Respuesta sin archivo")
                body.byteStream().use { input ->
                    resolver.openOutputStream(outputUri)?.use { output ->
                        input.copyTo(output)
                    } ?: throw Exception("No se pudo abrir Downloads")
                }
            }
            outputUri
        } else {
            throw Exception("Esta funcionalidad requiere Android Q o superior")
        }
    }

    override suspend fun downloadAudioToFolder(ytUrl: String, fileName: String, folderUri: Uri): Uri = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val documentUri = DocumentsContract.buildDocumentUriUsingTree(
            folderUri,
            DocumentsContract.getTreeDocumentId(folderUri)
        )
        val outputUri = DocumentsContract.createDocument(
            resolver,
            documentUri,
            "audio/mpeg",
            "${safeFileName(fileName)}.mp3"
        ) ?: throw Exception("No se pudo crear el archivo")

        api.reqData(ytUrl, "download-audio").use { response ->
            if (!response.isSuccessful) {
                throw Exception("Respuesta del servidor: ${response.code}")
            }
            val body = response.body ?: throw Exception("Respuesta sin archivo")
            body.byteStream().use { input ->
                resolver.openOutputStream(outputUri)?.use { output ->
                    input.copyTo(output)
                } ?: throw Exception("No se pudo abrir la carpeta seleccionada")
            }
        }
        outputUri
    }

    private fun safeFileName(fileName: String): String {
        return fileName
            .replace("/", "_")
            .replace("\\", "_")
            .replace(":", "_")
            .ifBlank { "audio" }
    }
}
