package com.example.twitterclone.network

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User
import hprose.client.HproseClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.net.URL

class HprosePerAuthor(val mid: MimeiId) {
    private var baseUrl = "http://localhost:8081"

    // Keys within the mimei of each tweet
    private val TWT_CONTENT_KEY = "content_of_tweet"  // content key within the Mimei

    // Keys within the mimei of the user's database
    private val TWT_LIST_KEY = "list_of_tweets_mid"
    private val OWNER_DATA_KEY = "data_of_node_owner"     // account data of user
    private val FOLLOWINGS_KEY = "list_of_followings_mid"
    private val FOLLOWERS_KEY = "list_of_followers_mid"

    private var sid: String = ""
//    private var mmUser: User
    private val gadget = Gadget()
    private val client: HproseService by lazy {
        HproseClient.create("$baseUrl/webapi/").useService(HproseService::class.java)
    }

    // Initialize lazily, also used as UserId

//    init {
//        val pair: Pair<URL, String?>? = runBlocking { gadget.getFirstReachableUri(mid) }
//        baseUrl = tmpUrl
//        mmUser = Json.decodeFromString(str)
//    }
}