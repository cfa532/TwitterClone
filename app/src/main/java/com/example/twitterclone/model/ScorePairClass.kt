package com.example.twitterclone.model

interface ScorePair {
    val score: Long
    val member: String
}

data class ScorePairClass(override var score: Long = 0, override var member: String = "") : ScorePair