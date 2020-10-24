package com.rokobit.myrouter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.rokobit.myrouter.data.entity.UserEntity
import com.rokobit.myrouter.myDatabase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers

class MainViewModel : CommandsViewModel() {

    private val userDao = myDatabase.userDao()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("MainViewModel", "download info", exception)
    }

    val userList = userDao.list()

    fun user(userID: Long) = liveData {
        emit(
            userDao.user(userID)
        )
    }

    fun save(userEntity: UserEntity): LiveData<Long> {
        return liveData(Dispatchers.IO) {
            emit(
                if (userEntity.id == 0L)
                    userDao.add(userEntity)
                else {
                    userDao.update(userEntity)
                    userEntity.id
                }
            )
        }
    }

}