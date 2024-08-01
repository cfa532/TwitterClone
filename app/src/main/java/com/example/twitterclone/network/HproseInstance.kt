package com.example.twitterclone.network

import android.content.Context
import android.net.Uri
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
import kotlinx.serialization.json.jsonArray
import java.io.FileNotFoundException
import java.io.InputStream
import java.math.BigInteger
import java.net.URL

interface HproseService {
    fun getVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun login(ppt: String): Map<String, String>
    fun getVar(sid: String, name: String, arg1: String? = null, arg2: String? = null): String
    fun mmCreate(
        sid: String,
        appId: String,
        ext: String,
        mark: String,
        tp: Byte,
        right: Long
    ): MimeiId

    fun mmOpen(sid: String, mid: MimeiId, version: String): String
    fun mmBackup(
        sid: String,
        mid: MimeiId,
        memo: String = "",
        ref: String = ""
    ) // Add default value for 'ref'

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
    const val BASE_URL = "http://192.168.1.103:8081"

    private const val CHUNK_SIZE = 10 * 1024 * 1024 // 10MB in bytes
    private const val APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    private const val APP_EXT = "com.example.twitterclone"
    private const val APP_MARK = "version 0.0.2"

    // Keys within the mimei of each tweet
    private const val TWT_CONTENT_KEY = "content_of_tweet"  // content key within the Mimei
    private const val TWT_LIKED_COUNT = "count_of_likes"

    // Keys within the mimei of the user's database
    private const val TWT_LIST_KEY = "list_of_tweets_mid"
    private const val OWNER_DATA_KEY = "data_of_node_owner"     // account data of user
    private const val FOLLOWINGS_KEY = "list_of_followings_mid"
    private const val FOLLOWERS_KEY = "list_of_followers_mid"

    private var sid: String = ""
    private val client: HproseService by lazy {
        HproseClient.create("$BASE_URL/webapi/").useService(HproseService::class.java)
    }

    // Initialize lazily, also used as UserId
    lateinit var appMid: MimeiId
    private lateinit var appUser: User
    private val gadget = Gadget()

    // Initialize the Hprose instance and establish a session.
    fun initialize() {
        try {
            val ppt = client.getVarByContext("", "context_ppt")
            val result = client.login(ppt)
            sid = result["sid"].toString()
            println("Leither ver: " + client.getVar("", "ver"))
//            println("IPS: " + client.getVar("", "mmprovsips", "ejEx2oIEJGHHRGyYCzYCBxLkQrg"))
            println("Login result = $result")

            // Initialize the app's main MimeiId after successful login.
            // it should stay the same for the same user, therefore also used as UserId
            appMid = client.mmCreate(sid, APP_ID, APP_EXT, APP_MARK, 2, 120022788)
            println("App mid=$appMid")

            // if this is the 1st time user login, the appMid is empty, cannot open Last ver
            client.mmOpen(sid, appMid, "cur").let {
                val user = client.get(it, OWNER_DATA_KEY)
                println("User data=$user")
                if (user == null && sid != "") {
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
        } catch (e: Exception) {
            Log.e("HproseInstance.initialize", e.toString())
            throw e
        }
    }

    // operation too heavy
    private suspend fun getUser(mimeiId: MimeiId): Result<Pair<URL, User>> {
        val providerData = client.getVar("", "mmprovsips", mimeiId)
        return try {
            val providerLists = Json.parseToJsonElement(providerData).jsonArray
            when {
                providerLists.isNotEmpty() -> {
                    val ipAddresses = providerLists[0].jsonArray.map { it.jsonArray }
                    gadget.getFirstReachableUri(ipAddresses, mimeiId)?.let { (url, jsonData) ->
                        jsonData?.let {
                            Result.success(Pair(url, Json.decodeFromString<User>(jsonData)))
                        } ?: Result.failure(InvalidProviderDataException("Missing user data for $mimeiId"))
                    } ?: Result.failure(ProviderNotFoundException("No reachable provider found for $mimeiId"))
                }
                else -> Result.failure(InvalidProviderDataException("Invalid provider data for $mimeiId: $providerData"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    class ProviderNotFoundException(message: String) : Exception(message)
    class InvalidProviderDataException(message: String) : Exception(message)

    suspend fun getUserData(userId: MimeiId = appMid): User? {
        return runCatching {
            val baseUrl = getUser(userId)
            client.mmOpen("", userId, "last").let {
                client.get(it, OWNER_DATA_KEY)?.let { userData ->
                    Json.decodeFromString<User>(userData as String) // Assuming Json is a kotlinx.serialization object
                }
            }
        }.onFailure { e ->
            Log.e("HproseInstance.getUserData", "Failed to get user data for userId: $userId", e)
        }.getOrNull()
    }

    fun setUserData(user: User) {
        try {
            client.mmOpen(sid, appMid, "cur").let {
                client.set(it, OWNER_DATA_KEY, Json.encodeToString(user))
                client.mmBackup(sid, appMid, "")
                client.mimeiPublish(sid, "", appMid)
            }
        } catch (e: Exception) {
            Log.e("HproseInstance.setUserData", e.toString())
        }
    }

    // get Ids of users who the current user is following
    fun getFollowings(userId: MimeiId = appMid): List<MimeiId> =
        try {
            client.mmOpen("", userId, "last").run {
                client.get(this, FOLLOWINGS_KEY)?.let { keys ->
                    (keys as? List<*>)?.mapNotNull { it as? MimeiId }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("HproseInstance.getFollowings", e.toString())
            emptyList()
        }

    // get tweets of a given author in a given span of time
    // if end is null, get all tweets
    fun getTweets(
        authorId: MimeiId = appMid,
        tweets: MutableList<Tweet>,
        startTimestamp: Long,
        endTimestamp: Long?
    ) = try {
        client.mmOpen("", authorId, "last").also {
            client.zRevRange(it, TWT_LIST_KEY, 0, -1).forEach { e ->
                val sp = e as Map<*, *>
                val score = (sp["score"] as BigInteger).toLong()
                val tweetId = sp["member"] as MimeiId
                if (score <= startTimestamp && (endTimestamp == null || score > endTimestamp)) {
                    // check if the tweet is in the tweets already.
                    if (tweets.none { t -> t.mid == tweetId }) {
                        client.mmOpen("", tweetId, "last").also { mmsid ->
                            client.get(mmsid, TWT_CONTENT_KEY)?.let { content ->
                                tweets += Json.decodeFromString<Tweet>(content as String)
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("HproseInstance.getTweets", e.toString())
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
        var offset = 0
        inputStream.use { stream ->
            val buffer = ByteArray(CHUNK_SIZE)
            var bytesRead: Int
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                client.mfSetData(fsid, buffer, offset)
                offset += bytesRead
            }
        }
        return client.mfTemp2Ipfs(
            fsid,
            appMid
        )    // Associate the uploaded data with the app's main Mimei
    }

    suspend fun uploadAttachments(context: Context, attachments: List<Uri>): List<MimeiId> {
        return attachments.mapNotNull { uri ->
            uploadFile(context, uri)
        }
    }

    private suspend fun uploadFile(context: Context, uri: Uri): MimeiId? {
        return withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    uploadToIPFS(inputStream)
                } ?: throw FileNotFoundException("File not found for URI: $uri")
            }.getOrElse { e ->
                Log.e("HproseInstance.uploadFile", "Failed to upload file: $uri", e)
                null
            }
        }
    }

    private suspend fun uploadFile(json: String): MimeiId =
        withContext(Dispatchers.IO) {
            client.mfOpenTempFile(sid).let { fsid ->
                client.mfSetData(fsid, json.toByteArray(), 0)
                client.mfTemp2Ipfs(fsid, appMid)
            }
        }
}