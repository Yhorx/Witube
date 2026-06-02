package com.example.witube

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .callTimeout(6, TimeUnit.MINUTES)
        .build()

    fun reqData(ytUrl: String,path: String): Response{
        val jsonBody = JSONObject()
            .put("url", ytUrl)
            .toString()

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://witube.onrender.com/${path}")
            .post(requestBody)
            .build()

        val jsonText = client.newCall(request).execute()

        return jsonText
    }

    fun getAudioInfo(ytUrl: String): AudioInfo{
                val data = reqData(ytUrl,"info-audio")
                    .use { response ->
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

            return AudioInfo(
                title = title,
                duration = duration,
                artist,
                thumbnail = thumbnail
            )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getDownloadAudio(context: Context, ytUrl: String, fileName: String): Uri {
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

        reqData(ytUrl,"download-audio").use { response ->
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

        return outputUri
    }

    fun getDowloadAudioToFolder(context: Context, ytUrl: String, fileName: String, folderUri: Uri): Uri {
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

        reqData(ytUrl,"download-audio").use { response ->
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

        return outputUri
    }

    private fun safeFileName(fileName: String): String {
        return fileName
            .replace("/", "_")
            .replace("\\", "_")
            .replace(":", "_")
            .ifBlank { "audio" }
    }
}
