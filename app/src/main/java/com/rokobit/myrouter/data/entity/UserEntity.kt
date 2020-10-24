package com.rokobit.myrouter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val serverIP: String,
    val port: Int = 22,
    val login: String,
    val password: String,
    val speedIP: String
)