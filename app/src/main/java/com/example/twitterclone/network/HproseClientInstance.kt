package com.example.twitterclone.network

import android.content.Intent
import com.example.twitterclone.R
import com.example.twitterclone.model.MimeiId
import hprose.client.HproseClient

interface HproseService {
    fun GetVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun Login(ppt: String): Map<String, String>
    fun GetVar(sid: String, name: String): String
    fun MMCreate(sid: String,
                 appId: String = R.string.app_id.toString(),
                 ext: String = R.string.app_ext.toString(),
                 mark: String = R.string.app_mark.toString(),
                 tp: UByte = 2u,
                 right: UInt = 120022788u): MimeiId     // 0x07276704
    fun MMOpen(sid: String, mid: MimeiId, version: String): String
    fun MMBackup(sid: String, mid: MimeiId, memo: String = "", ref: String)
    fun MMAddRef(sid: String, mid: MimeiId, mimeiId: MimeiId)
    fun MMSetObject(fsid: String, obj: Any)
    fun MiMeiPublish(sid: String, memo: String, mid: MimeiId)
}

object HproseInstance {
    private const val BASE_URL = "http://192.168.1.103:8081/webapi/"
    private var sid: String = ""

    val PICK_FILE_REQUEST = 1
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*" // You can specify the type of files you want to allow
    }
//    startActivityForResult(intent, PICK_FILE_REQUEST)

    // Leither client handler
    private val client: HproseService by lazy {
        HproseClient.create(BASE_URL).useService(HproseService::class.java)
    }
    // Main database Mimei of this user. All the files mimei refer to this one.
    private val mid: String by lazy { client.MMCreate(sid) }

    fun initialize() {
        val ppt = client.GetVarByContext("", "context_ppt")
        val result = client.Login(ppt)
        sid = result["sid"].toString()
        println(client.GetVar("", "ver"))
        println("Login: $sid")
    }

    // Given an object, store it in mimei file and return the mimei Id.
    fun mmSetObject(obj: Any): MimeiId {
        // create a file mimei for a tweet
        val mid = client.MMCreate(sid = sid, mark = "{{auto}}", tp = 1u)
        // open Cur version of the mimei
        val fsid = client.MMOpen(sid, mid, "cur")
        // set the object into mimei
        client.MMSetObject(fsid, obj)
        // backup the mimei
        client.MMBackup(sid, mid, "","delref=true")
        // add ref to App's main database mimei which is published on the net
        client.MMAddRef(sid, HproseInstance.mid, mid)
        return mid
    }

    fun mmUpload2IPFS(ipfs: MimeiId) {
    }
}