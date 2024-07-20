package com.example.twitterclone.network

import hprose.client.HproseClient

interface HproseService {
    fun GetVarByContext(sid: String, context: String, args: String): String
    fun Login(ppt: String): Map<String, String>
    fun GetVar(sid: String, args: String): String
}

object HproseInstance {
    private const val BASE_URL = "http://0.0.0.0:8081/webapi/"

    val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }
}