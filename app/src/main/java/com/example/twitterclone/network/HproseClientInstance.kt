package com.example.twitterclone.network

import hprose.client.HproseClient

interface HproseService {
    fun GetVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun Login(ppt: String): Map<String, String>
    fun GetVar(sid: String, name: String): String
}

object HproseInstance {
    private const val BASE_URL = "http://192.168.1.103:8081/webapi/"

    val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }
}