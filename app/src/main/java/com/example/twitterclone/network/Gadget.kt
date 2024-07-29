package com.example.twitterclone.network

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.net.InetAddress
import java.net.URI
import java.net.UnknownHostException

class Gadget {
    fun getUriFromIp(ip: String): URI {
        val (host, port) = if (ip.startsWith("[")) { // IPv6 address
            val parts = ip.substring(1, ip.length - 1).split("]:")
            parts[0] to parts[1].toInt()
        } else { // IPv4 address
            val parts = ip.split(":")
            parts[0] to parts[1].toInt()
        }
        return URI("http://$host:$port")
    }

    private fun isReachable(host: String, port: Int, timeout: Int = 1000): Boolean {
        return try {
            InetAddress.getByName(host).isReachable(timeout)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFirstReachableUri(ipList: List<String>): URI? {
        return coroutineScope {
            ipList.map { ip ->
                async {
                    try {
                        val uri = getUriFromIp(ip)
                        if (isReachable(uri.host,uri.port)) uri else null
                    } catch (e: UnknownHostException) {
                        null // Handle invalid IP address
                    } catch (e: IllegalArgumentException) {
                        null // Handle invalid port
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