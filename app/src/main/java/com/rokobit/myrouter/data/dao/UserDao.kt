package com.rokobit.myrouter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rokobit.myrouter.data.entity.UserEntity

@Dao
interface UserDao {

    @Query("select * from userentity")
    fun list(): LiveData<List<UserEntity>>

    @Insert
    suspend fun add(userEntity: UserEntity): Long

    @Update
    fun update(userEntity: UserEntity)

    @Query("select * from userentity where id = :id limit 1")
    suspend fun user(id: Long): UserEntity?

}