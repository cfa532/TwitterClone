package com.example.twitterclone.network

import android.util.Log
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.ScorePair
import com.example.twitterclone.model.ScorePairClass
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.User
import hprose.client.HproseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream

interface HproseService {fun getVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun login(ppt: String): Map<String, String>
    fun getVar(sid: String, name: String, arg1: String? = null, arg2: String? = null): String
    fun mmCreate(sid: String, appId: String, ext: String, mark: String, tp: Byte, right: Long): MimeiId
    fun mmOpen(sid: String, mid: MimeiId, version: String): String
    fun mmBackup(sid: String, mid: MimeiId, memo: String = "", ref: String = "") // Add default value for 'ref'
    fun mmAddRef(sid: String, mid: MimeiId, mimeiId: MimeiId)
    fun mmSetObject(fsid: String, obj: Any)
    fun mimeiPublish(sid: String, memo: String, mid: MimeiId)
    fun mfOpenTempFile(sid: String): String
    fun mfTemp2Ipfs(fsid: String, ref: MimeiId): MimeiId
    fun mfSetCid(sid: String, mid: MimeiId, cid: MimeiId)
    fun mfSetData(fsid: String, data: ByteArray, offset: Int)
    fun set(sid: String, key: String, value: Any)
    fun get(sid: String, key: String): Any?
    fun hGet(sid: String, key: String, field: String): Any?
    fun hSet(sid: String, key: String, field: String, value: Any)
    fun hDel(sid: String, key: String, field: String)
    fun zAdd(sid: String, key: String, sp: ScorePair)
    fun zRevRange(sid: String, key: String, start: Long, end: Long): List<*>
}

// Encapsulate Hprose client and related operations in a singleton object.
object HproseInstance {
    private const val CHUNK_SIZE = 10 * 1024 * 1024 // 10MB in bytes
    private const val BASE_URL = "http://192.168.1.103:8081/webapi/"
    private const val APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    private const val APP_EXT = "com.example.twitterclone"
    private const val APP_MARK = "version 0.0.2"

    // Keys within the mimei of each tweet
    private const val TWT_CONTENT_KEY = "content_of_tweet"  // content key within the Mimei

    // Keys within the mimei of the user's database
    private const val TWT_LIST_KEY = "list_of_tweets_mid"
    private const val OWNER_DATA_KEY = "data_of_node_owner"     // account data of user
    private const val FOLLOWINGS_KEY = "list_of_followings_mid"
    private const val FOLLOWERS_KEY = "list_of_followers_mid"

    private var sid:String = ""
    private val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }

    // Initialize lazily, also used as UserId
    private lateinit var appMid: MimeiId
    private lateinit var appUser: User

    // Initialize the Hprose instance and establish a session.
    fun initialize() {
        val ppt = client.getVarByContext("", "context_ppt")
        val result = client.login(ppt)
        sid = result["sid"].toString()
        println("Leither ver: " + client.getVar("", "ver"))
        println("IPS: " + client.getVar("", "ips", "1-U-7NvW2hOWmyoiipkzno65so-"))
        println("Login result = $result")

        // Initialize the app's main MimeiId after successful login.
        // it should stay the same for the same user, therefore also used as UserId
        appMid = client.mmCreate(sid, APP_ID, APP_EXT, APP_MARK, 2, 120022788)
        println("App mid=$appMid")

        // if this is the 1st time user login, the appMid is empty, cannot open Last ver
        client.mmOpen(sid, appMid, "cur").let {
            val user = client.get(it, OWNER_DATA_KEY)
            println("User data=$user")
            if (user == null) {
                // first time run, init user data
                appUser = User(mid = appMid)
                client.set(it, OWNER_DATA_KEY, Json.encodeToString(appUser))
                client.set(it, FOLLOWINGS_KEY, listOf(appMid))  // always following oneself
                client.mmBackup(sid, appMid, "")
                client.mimeiPublish(sid, "", appMid)
            } else {
                appUser = Json.decodeFromString(user.toString())
            }
            println("App user=$appUser")
        }
    }

    fun getTweets(): List<Tweet> {
        val tweets = mutableListOf<Tweet>()
        try {
            client.mmOpen("", appMid, "last").let {
                client.get(it, FOLLOWINGS_KEY)?.let { fls ->
                    (fls as List<*>).forEach { uid ->
                        println("following uid=$uid")
                        client.mmOpen("", uid as MimeiId, "last").let { it1 ->
                            client.zRevRange(it1, TWT_LIST_KEY, 0, 3).let { list ->
                                list.forEach { l ->
                                    val sp = l as Map<*, *>
                                    client.mmOpen("", sp["member"] as MimeiId, "last").let { tmid ->
                                        client.get(tmid, TWT_CONTENT_KEY)?.let { content ->
                                            tweets += Json.decodeFromString(content.toString()) as Tweet
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return tweets
        } catch (e: Exception) {
            Log.e("HproseInstance.getTweets", e.toString())
            throw e
        }
    }

    // Store an object in a Mimei file and return its MimeiId.
    fun uploadTweet(t: Tweet): Tweet {
        client.mmCreate(sid, APP_ID, APP_EXT, t.content, 2, 120022788).let { mid ->
            t.mid = mid
            println("Created tweet mid=$mid $t")
            client.mmOpen(sid, mid, "cur").let {
                client.set(it, TWT_CONTENT_KEY, Json.encodeToString(t))
                client.mmBackup(
                    sid,
                    mid,
                    "",
                    "delref=true"
                ) // Use default memo, specify ref deletion
                client.mimeiPublish(sid, "", mid)       // publish tweet
                client.mmAddRef(sid, appMid, mid) // Reference the object from the app's main Mimei
            }

            // add the new tweet in user's tweet list
            client.mmOpen(sid, appMid, "cur").let {
                client.zAdd(it, TWT_LIST_KEY, ScorePairClass(System.currentTimeMillis(), mid))
                client.mmBackup(
                    sid,
                    appMid,
                    "",
                    "delref=true"
                ) // Use default memo, specify ref deletion
                client.mimeiPublish(sid, "", appMid)    // publish tweet list
            }
        }
        return t
    }

    // Upload data from an InputStream to IPFS and return the resulting MimeiId.
    fun uploadToIPFS(inputStream: InputStream): MimeiId {
        val fsid = client.mfOpenTempFile(sid)
        var start = 0
        try {
            inputStream.use { inputStream1 ->
                val buffer = ByteArray(CHUNK_SIZE)
                var bytesRead: Int
                while (inputStream1.read(buffer).also { bytesRead = it } != -1) {
                    client.mfSetData(fsid, buffer.copyOfRange(0, bytesRead), start)
                    start += bytesRead
                }
            }
            return client.mfTemp2Ipfs(fsid, appMid) // Associate the uploaded data with the app's main Mimei
        } catch (e: Exception) {
            throw e
        }
    }
}