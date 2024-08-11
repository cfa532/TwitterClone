package com.example.twitterclone.network

import android.util.Log
import com.example.twitterclone.model.HproseInstance.TWBE_APP_ID
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException

object Gadget {
    private var httpClient: OkHttpClient? = null

    fun initialize( httpClient: OkHttpClient) {
        this.httpClient = httpClient
    }

    fun downloadFileHeader(url: String, byteCount: Int = 1024): ByteArray? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .head()
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val contentLength = response.header("Content-Length")?.toInt() ?: byteCount
                val requestWithRange = Request.Builder()
                    .url(url)
                    .addHeader("Range", "bytes=0-${minOf(contentLength, byteCount - 1)}")
                    .build()
                val responseWithRange = client.newCall(requestWithRange).execute()
                responseWithRange.body?.byteStream()?.readBytes()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun detectMimeTypeFromHeader(header: ByteArray?): String? {
        if (header == null) return null
        return when {
            header.startsWith(byteArrayOf(0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70)) -> "video/mp4"
            header.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte())) -> "image/jpeg"
            header.startsWith(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)) -> "image/png"
            header.startsWith(byteArrayOf(0x49, 0x44, 0x33)) -> "audio/mpeg" // MP3
            header.startsWith(byteArrayOf(0x4F, 0x67, 0x67, 0x53)) -> "audio/ogg" // OGG
            header.startsWith(byteArrayOf(0x66, 0x4C, 0x61, 0x43)) -> "audio/flac" // FLAC
            header.startsWith(byteArrayOf(0x52, 0x49, 0x46, 0x46)) && header.sliceArray(8..11).contentEquals(byteArrayOf(0x57, 0x41, 0x56, 0x45)) -> "audio/wav" // WAV
            else -> "application/octet-stream"
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        for (i in prefix.indices) {
            if (this[i] != prefix[i]) return false
        }
        return true
    }

    private fun isReachable(mid: MimeiId, uri: URL, timeout: Int = 1000): User? {
        try {
            val method = "get_author_core_data"
            val url =
                "http://${uri.path}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&userid=$mid"
            val request = Request.Builder().url(url).build()
            val response = com.example.twitterclone.httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return null
                val user = Json.decodeFromString<User>(responseBody)
                user.baseUrl = uri.path
                return user
            }
        } catch (e: Exception) {
            Log.e("Gadget.isReachable", e.toString())
        }
        return null
    }

    // In Pair<URL, String?>?, where String is JSON of Mimei content
    suspend fun getFirstReachableUri(ipList: List<JsonArray>, mid: MimeiId): User? = coroutineScope {
        val deferreds = ipList.map { ip ->
            async {
                val uri = getUriFromIp(ip[0].toString().replace("^\"|\"$".toRegex(), ""))
                isReachable(mid, uri)
            }
        }
        deferreds.awaitAll().firstOrNull { it != null }
    }

    // ip is in the format of "125.229.161.122:8081" or "[2001:b011:e606:98c5:3be9:a5f3:39c4:ff36]:8081"
    private fun getUriFromIp(ip: String): URL {
        val (host, port) = if (ip.startsWith("[")) { // IPv6 address
            val parts = ip.substring(1, ip.length - 1).split("]:")
            parts[0] to parts[1].toInt()
        } else { // IPv4 address
            val parts = ip.split(":")
            parts[0] to parts[1].toInt()
        }
        return URL("http://$host:$port")
    }
}