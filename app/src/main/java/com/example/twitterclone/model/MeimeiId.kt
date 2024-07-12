package com.example.twitterclone.model

typealias MimeiId = String

fun isValidMimeiId(id: MimeiId): Boolean {
    return id.length == 27 || id.length == 64
}