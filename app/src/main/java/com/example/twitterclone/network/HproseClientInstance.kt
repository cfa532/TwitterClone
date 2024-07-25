package com.example.twitterclone.network

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.ScorePair
import com.example.twitterclone.model.ScorePairClass
import com.example.twitterclone.model.Tweet
import hprose.client.HproseClient
import java.io.InputStream

interface HproseService {fun getVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun login(ppt: String): Map<String, String>
    fun getVar(sid: String, name: String): String
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
    fun get(sid: String, key: String): Any
    fun hGet(sid: String, key: String, field: String): Any
    fun hSet(sid: String, key: String, field: String, value: Any)
    fun hDel(sid: String, key: String, field: String)
    fun zAdd(sid: String, key: String, sp: ScorePair)
}

// Encapsulate Hprose client and related operations in a singleton object.
object HproseInstance {
    private const val CHUNK_SIZE = 10 * 1024 * 1024 // 10MB in bytes
    private const val BASE_URL = "http://192.168.1.103:8081/webapi/"
    private const val APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    private const val APP_EXT = "com.example.twitterclone"
    private const val APP_MARK = "version 0.0.1"

    private const val TWT_CONTENT_KEY = "content_of_tweet"  // content key within the Mimei
    private const val TWTS_LIST_KEY = "list_of_tweets"       // list key within the Mimei

    private var sid:String = ""
    private val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }

    // Initialize lazily to ensure it's only created when needed.
    private lateinit var appMid: MimeiId

    // Initialize the Hprose instance and establish a session.
    fun initialize() {
        val ppt = client.getVarByContext("", "context_ppt")
        val result = client.login(ppt)
        sid = result["sid"].toString()
        println("Leither ver: " + client.getVar("", "ver"))
        println("Login result = $result")

        // Initialize the app's main MimeiId after successful login.
        appMid = client.mmCreate(sid, APP_ID, APP_EXT, APP_MARK, 2, 120022788)
        println("App mid=$appMid")
    }

    // Store an object in a Mimei file and return its MimeiId.
    fun uploadTweet(t: Tweet): Tweet {
        val mid = client.mmCreate(sid, APP_ID, APP_EXT, t.content, 2, 120022788)
        t.mid = mid
        println("Created tweet mid=$mid $t")
        var mmsid = client.mmOpen(sid, mid, "cur")
        client.set(mmsid, TWT_CONTENT_KEY, t)
        client.mmBackup(sid, mid, "", "delref=true") // Use default memo, specify ref deletion
        client.mmAddRef(sid, appMid, mid) // Reference the object from the app's main Mimei

        // add the new tweet in user's tweet list
        mmsid = client.mmOpen(sid, appMid, "cur")
        client.zAdd(mmsid, TWTS_LIST_KEY, ScorePairClass(System.currentTimeMillis(), mid))
        client.mmBackup(sid, appMid, "", "delref=true") // Use default memo, specify ref deletion
        client.mimeiPublish(sid, "", mid)

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