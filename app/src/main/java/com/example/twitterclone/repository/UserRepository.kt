package com.example.twitterclone.repository

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.User

class UserRepository {
    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }

    fun getUser(userMid: MimeiId): User {
        return users.find { it.mid == userMid } ?: throw IllegalArgumentException("User not found")
    }

    fun updateUser(updatedUser: User) {
        val index = users.indexOfFirst { it.mid == updatedUser.mid }
        if (index != -1) {
            users[index] = updatedUser
        } else {
            throw IllegalArgumentException("User not found")
        }
    }

    fun getUsers(): List<User> {
        return users
    }
}