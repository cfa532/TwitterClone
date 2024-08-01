package com.example.twitterclone.network

import android.net.Uri
import android.util.Log
import com.example.twitterclone.model.MimeiId
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

const val MIMEI_ID_LENGTH = 27

class Gadget {
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

    private fun isReachable(mid: MimeiId, host: String, port: Int, timeout: Int = 1000): Pair<URL, String?>? {
        return try {
            val url = if (mid.length > MIMEI_ID_LENGTH) {
                URL("http://$host:$port/ipfs/$mid")
            } else {
                URL("http://$host:$port/mm/$mid")
            }
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Pair(url, response)
            } else {
                null // Or handle other response codes as needed
            }
        } catch (e: TimeoutException) {
            null // Handle timeout
        } catch (e: Exception) {
            null // Handle other exceptions
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

//    val ipList = listOf(
//        "125.229.161.122:8081",
//        "[2001:b011:e606:98c5:cc8:da02:da01:821b]:8081",
//        "192.168.1.103:8081"
//    )
//
//    runBlocking {
//        val firstReachable = getFirstReachableUri(ipList)
//        println("First reachable URI:$firstReachable")
//    }
}