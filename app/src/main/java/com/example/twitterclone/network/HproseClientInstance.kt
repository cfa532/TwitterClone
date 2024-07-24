package com.example.twitterclone.network

import com.example.twitterclone.model.MimeiId
import hprose.client.HproseClient
import java.io.InputStream

interface HproseService {
    fun getVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun login(ppt: String): Map<String, String>
    fun getVar(sid: String, name: String): String
    fun mmCreate(sid: String, appId: String, ext: String, mark: String, tp: Byte, right: Long): MimeiId     // 0x07276704
    fun mmOpen(sid: String, mid: MimeiId, version: String): String
    fun mmBackup(sid: String, mid: MimeiId, memo: String = "", ref: String)
    fun mmAddRef(sid: String, mid: MimeiId, mimeiId: MimeiId)
    fun mmSetObject(fsid: String, obj: Any)
    fun mimeiPublish(sid: String, memo: String, mid: MimeiId)
    fun mfOpenTempFile(sid: String): String

    // Given a file sessionID, return the IPFS CID that references a ref Id.
    fun mfTemp2Ipfs(fsid: String, ref: MimeiId): MimeiId
    fun mfSetCid(sid: String, mid: MimeiId, cid: MimeiId)
    fun mfSetData(fsid: String, data: ByteArray, offset: Int)
}

object HproseInstance {
    private const val CHUNK_SIZE = 10 * 1024 * 1024 // 10MB in bytes
    private const val BASE_URL = "http://192.168.1.103:8081/webapi/"
    private const val APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    private const val APP_EXT = "com.example.twitterclone"
    private const val APP_MARK = "version 0.0.1"
    private var sid: String = ""

    // Leither client handler
    private val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }
    // Main database Mimei of this user. All the files mimei refer to this one.
    private val mid: MimeiId by lazy {
        client.mmCreate(sid, APP_ID, APP_EXT, APP_MARK, 2, 120022788)
    }

    fun initialize() {
        val ppt = client.getVarByContext("", "context_ppt")
        val result = client.login(ppt)
        sid = result["sid"].toString()
        println("Leither ver: " + client.getVar("", "ver"))
        println("Login result = $result")
        println("App mid=$mid")     // required to init App mid. Must have.
    }

    // Given an object, store it in mimei file and return the mimei Id.
    fun mmSetObject(obj: Any): MimeiId {
        // create a file mimei for a tweet
        val mid = client.mmCreate(sid, APP_ID, APP_EXT, "{{auto}}", 2, 120022788)
        // open Cur version of the mimei
        val fsid = client.mmOpen(sid, mid, "cur")
        // set the object into mimei
        client.mmSetObject(fsid, obj)
        // backup the mimei
        client.mmBackup(sid, mid, "","delref=true")
        // add ref to App's main database mimei which is published on the net
        client.mmAddRef(sid, HproseInstance.mid, mid)
        return mid
    }

    // taken an InputStream and return the IPFS cid
    fun mmUpload2IPFS(inputStream: InputStream): MimeiId {
        val fsid = client.mfOpenTempFile(sid)
        var start = 0
        inputStream.use { inputStream1 ->
            val buffer = ByteArray(CHUNK_SIZE)
            var bytesRead: Int
            while (inputStream1.read(buffer).also { bytesRead = it } != -1) {
                client.mfSetData(fsid, buffer.copyOfRange(0, bytesRead), start)
                start += bytesRead
            }
        }
        return client.mfTemp2Ipfs(fsid, mid)
    }

}