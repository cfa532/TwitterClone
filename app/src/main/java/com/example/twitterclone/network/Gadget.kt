package com.example.twitterclone.network

import android.util.Log
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.HproseInstance.TWBE_APP_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import java.net.UnknownHostException

object Gadget {
    private var httpClient: OkHttpClient? = null

    fun initialize( httpClient: OkHttpClient) {
        this.httpClient = httpClient
    }

    fun detectFileType(url: String): String? {
        val request = Request.Builder().url(url)
            .head() // Only fetch headers
            .build()

        val response = httpClient?.newCall(request)?.execute()
        if (response?.isSuccessful == true) {
            val contentType = response.header("Content-Type")
            return contentType?.toMediaTypeOrNull()?.type
        }
        return null
    }

    private suspend fun isReachable(mid: MimeiId, host: String, port: Int, timeout: Int = 1000): Pair<URL, String?>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "http://$host:$port/entry?&aid=$TWBE_APP_ID&ver=last&entry=get_author_preview&authorid=$mid"
                val request = Request.Builder().url(url).build()
                val response = httpClient?.newCall(request)?.execute()
                Pair(URL("http://$host:$port"), response?.body?.string())
            } catch (e: Exception) {
                null // Handle other exceptions
            }
        }
    }

    // In Pair<URL, String?>?, where String is JSON of Mimei content
    suspend fun getFirstReachableUri(ipList: List<JsonArray>, mid: MimeiId): Pair<URL, String?>? {
        return coroutineScope {
            ipList.map { ip: JsonArray ->
                async {
                    try {
                        val uri = getUriFromIp(ip[0].toString().replace("^\"|\"$".toRegex(), ""))
                        val result = isReachable(mid, uri.host, uri.port) //Get Pair from isReachable
                        result // Return the Pair if not null
                    } catch (e: UnknownHostException) {
                        Log.e("UnknownHostException", "Unknown host: ${e.message}")
                        null
                    } catch (e: IllegalArgumentException) {
                        Log.e("IllegalArgumentException", "Invalid URL: ${e.message}")
                        null
                    }
                }
            }.firstOrNull {
                it.await() != null
            }?.await()
        }
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