package com.example.twitterclone.network

import android.util.Log
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.HproseInstance.TWBE_APP_ID
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import java.net.URL
import java.net.UnknownHostException

class Gadget (
    private val httpClient: HttpClient = HttpClient(CIO)
) {
    private suspend fun isReachable(mid: MimeiId, host: String, port: Int, timeout: Int = 1000): Pair<URL, String?>? {
        return withContext(Dispatchers.IO) {
            try {
                httpClient.get("http://$host:$port/entry?&aid=$TWBE_APP_ID&ver=last&entry=get_author_preview&authorid=$mid") {
                    contentType(ContentType.Application.Json)
                }.bodyAsText().let {
                    Pair(URL("http://$host:$port"), it)
                }
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