package com.example.twitterclone.model

import kotlinx.serialization.*

@Serializable
data class ScorePair(
    val score: Long = 0,
    val member: String = ""
)