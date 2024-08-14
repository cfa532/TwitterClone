package com.example.twitterclone.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.twitterclone.R
import com.example.twitterclone.httpClient
import com.example.twitterclone.network.Gadget
import com.google.gson.Gson
import hprose.client.HproseClient
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import okhttp3.Request
import java.io.FileNotFoundException
import java.io.InputStream
import java.math.BigInteger
import java.net.URLEncoder

// Encapsulate Hprose client and related operations in a singleton object.
object HproseInstance {
    private const val BASE_URL = "http://10.0.2.2:8081"
    const val TWBE_APP_ID = "d4lRyhABgqOnqY4bURSm_T-4FZ4"

    private const val CHUNK_SIZE = 50 * 1024 * 1024 // 10MB in bytes
    private const val APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    private const val APP_EXT = "com.example.twitterclone"
    private const val APP_MARK = "version 0.0.2"

    // Keys within the mimei of each tweet
    private const val TWT_CONTENT_KEY = "core_data_of_tweet"  // content key within the Mimei
    private const val TWT_LIKED_COUNT = "count_of_likes"

    // Keys within the mimei of the user's database
    private const val TWT_LIST_KEY = "list_of_tweets_mid"
    private const val OWNER_DATA_KEY = "data_of_author"     // account data of user
    private const val FOLLOWINGS_KEY = "list_of_followings_mid"
    private const val FOLLOWERS_KEY = "list_of_followers_mid"

    private val client: HproseService by lazy {
        HproseClient.create("$BASE_URL/webapi/").useService(HproseService::class.java)
    }

    private var sid = ""

    @Serializable
    data class TempJson(
        var sid: String = "",
        var mid: String = ""
    )

    val appUser: User by lazy {
        InMemoryData.users.find { it.mid == appMid } ?: runBlocking {
            withContext(IO) {
                val method = "get_author_core_data"
                val url = "$BASE_URL/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&userid=$appMid"
                val request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                val user = Json.decodeFromString<User>(response.body?.string() ?: "")
                user.baseUrl = BASE_URL
                InMemoryData.users.add(user)
                user
            }
        }
    }

    // Initialize lazily, also used as UserId
    private val appMid: MimeiId by lazy {
        runBlocking {   // necessary for the whole App
            withContext(IO) {
                val method = "get_app_mid"
                val url = "$BASE_URL/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method"
                val request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                val json = Json.decodeFromString<TempJson>(response.body?.string() ?: "")
                sid = json.sid
                json.mid
            }
        }
    }

    // Get base url where user data can be accessed, and user data
    private suspend fun getUserBase(userId: MimeiId): User? {
        // check if user data has been read
        InMemoryData.users.find { it.mid == userId }?.let { user -> return user }

        val providers = client.getVar("", "mmprovsips", userId)
        val providerList = Json.parseToJsonElement(providers).jsonArray
        if (providerList.isNotEmpty()) {
            println(providerList)
            val ipAddresses = providerList[0].jsonArray.map { it.jsonArray }
            Gadget.getFirstReachableUri(ipAddresses, userId)?.let { u ->
                InMemoryData.users.add(u)
                println("Get userbase=${InMemoryData.users}")
                return u
            }
        }
        return null
    }

    suspend fun getUserData(userId: MimeiId = appMid): User? {
        return runCatching {
            // get each user data based on its node ip
            val user = getUserBase(userId) ?: return null
            client.mmOpen("", userId, "last").let {
                client.get(it, OWNER_DATA_KEY)?.let { userData ->
                    userData as User
                }
            }
        }.onFailure { e ->
            Log.e("HproseInstance.getUserData", "Failed to get user data for userId: $userId", e)
        }.getOrNull()
    }

    fun setUserData(user: User) {
        // use Json here, so that null attributes in User are ignored. On the server-side, only set attributes
        // that have value in incoming data.
        val method = "set_author_core_data"
        val url = "${user.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&user=${
            Json.encodeToString(user)
        }"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.d("HproseInstance.setUserData", "Set user data error")
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
    suspend fun getTweetList(
        authorId: MimeiId,      // author ID of the tweets
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
                        getTweet(tweetId, authorId)?.let { t -> tweets += t }
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("HproseInstance.getTweets", e.toString())
    }

    private suspend fun getTweet(tweetId: MimeiId, authorId: MimeiId): Tweet? {
        val author = getUserBase(authorId) ?: return null
        val method = "get_tweet"

        // there should be a function to get baseUrl of the tweet's author
        val url =
            "${author.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&tweetid=$tweetId&userid=${appUser.mid}"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string()?.let { json ->
                println("getTweet=$json")
                val tweet = Gson().fromJson(json, Tweet::class.java)
                tweet.author = author
                tweet.isPrivate = false

                tweet.originalTweetId?.let {
                    val rt = InMemoryData._tweets.value.find { t -> t.mid == tweet.originalTweetId }
                    rt?.let { it1 ->
                        // isPrivate could be null
                        tweet.originalAuthor = it1.author
                        tweet.originalTweet = it1;
                        return tweet
                    }
                    tweet.originalTweet = tweet.originalAuthorId?.let { it1 -> getTweet(it, it1) } ?: return null
                    tweet.originalAuthor = tweet.originalTweet!!.author
                    InMemoryData._tweets.update { listOf(tweet.originalTweet!!) }
                }
                InMemoryData._tweets.update { listOf(tweet) }
                return tweet
            }
        }
        return null
    }

    // Store an object in a Mimei file and return its MimeiId.
    fun uploadTweet(tweet: Tweet, commentOnly: Boolean = false): Tweet? {
        val method = "upload_tweet"

        // make a copy of input tweet and remove attributes that is for display only.
        val t = tweet.copy()
        t.originalTweet = null
        t.author = null
        t.originalAuthor = null
        t.favorites = null

        val json = URLEncoder.encode(Json.encodeToString(t), "utf-8")
        val url =
            "${appUser.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&tweet=$json&commentonly=$commentOnly"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            tweet.mid = response.body?.string() ?: return null
            tweet.author = appUser
            return tweet
        }
        return null
    }

    fun toggleRetweet(tweet: Tweet): Tweet? {
        val method = "retweet"
        val t = tweet.copy()
        t.originalTweet = null
        t.author = null
        t.originalAuthor = null
        t.favorites = null

        val json = URLEncoder.encode(Json.encodeToString(t), "utf-8")
        val url =
            "${appUser.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&retweet=$json"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return null
            val gson = Gson()
            return gson.fromJson(responseBody, Tweet::class.java)
        }
        return null
    }

    fun likeTweet(tweet: Tweet): Tweet? {
        val author = tweet.author ?: return null
        val method = "liked_count"
        val url =
            "${author.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&tweetid=${tweet.mid}&userid=${appUser.mid}"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return tweet
            val gson = Gson()
            val res = gson.fromJson(responseBody, Map::class.java) as Map<*, *>

            // return a new object for recomposition to work.
            tweet.favorites?.set(UserFavorites.TWEET.ordinal, res["hasLiked"] as Boolean)
            return tweet.copy(
                likeCount = (res["count"] as Double).toInt()
            )
        }
        return tweet
    }

    fun bookmarkTweet(tweet: Tweet): Tweet? {
        val author = tweet.author ?: return null
        val method = "bookmark"
        val url =
            "${author.baseUrl}/entry?&aid=$TWBE_APP_ID&ver=last&entry=$method&tweetid=${tweet.mid}&userid=${appUser.mid}"
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return tweet
            val gson = Gson()
            val res = gson.fromJson(responseBody, Map::class.java) as Map<*, *>

            tweet.favorites?.set(UserFavorites.BOOKMARK.ordinal, res["hasBookmarked"] as Boolean)
            return tweet.copy(bookmarkCount = (res["count"] as Double).toInt())
        }
        return tweet
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
        val cid = client.mfTemp2Ipfs(
            fsid,
            appMid
        )    // Associate the uploaded data with the app's main Mimei
        println("cid=$cid")
        return cid
    }

    suspend fun uploadAttachments(context: Context, attachments: List<Uri>): List<MimeiId> {
        return attachments.mapNotNull { uri ->
            uploadFile(context, uri)
        }
    }

    private suspend fun uploadFile(context: Context, uri: Uri): MimeiId? {
        return withContext(IO) {
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

    fun getMediaUrl(mid: MimeiId?, baseUrl: String): Any {
        if (mid?.isNotEmpty() == true) {
            return if (mid.length > 27) {
                "$baseUrl/ipfs/$mid"
            } else {
                "$baseUrl/mm/$mid"
            }
        }
        return R.drawable.ic_user_avatar
    }
}

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