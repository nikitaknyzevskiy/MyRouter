package com.rokobit.myrouter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rokobit.myrouter.data.dao.UserDao
import com.rokobit.myrouter.data.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class MyDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao

}