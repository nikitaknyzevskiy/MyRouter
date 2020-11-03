package com.rokobit.myrouter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.rokobit.myrouter.data.DownloadSpeedInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import com.rokobit.myrouter.data.UploadSpeedInfo
import com.rokobit.myrouter.data.entity.UserEntity
import com.rokobit.myrouter.myDatabase
import com.rokobit.myrouter.repository.DeviceInfoRepository
import com.rokobit.myrouter.repository.SpeedInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

enum class SpeedProtocolType(val data: String) {
    UDP("udp"),
    TCP("tcp")
}

open class CommandsViewModel : ViewModel() {

    private var session: Session? = null

    private val deviceInfoRepository = DeviceInfoRepository {
        session
    }

    private val speedInfoRepository = SpeedInfoRepository {
        session
    }

    private val userDao = myDatabase.userDao()

    private var userEntity: UserEntity? = null

    var speedProtocolType = SpeedProtocolType.UDP

    val uploadLiveData = MutableLiveData<UploadSpeedInfo?>()

    val downloadLiveData = MutableLiveData<DownloadSpeedInfo?>()

    fun deviceInfo() = deviceInfoRepository.deviceInfo()

    fun isLinkRun() = deviceInfoRepository.isLinkRun()

    fun rateLink() = deviceInfoRepository.rateLink()

    fun isLinkCableOk() = deviceInfoRepository.isLinkCableOk()

    fun ipState() = deviceInfoRepository.ipState()

    fun ipInfo() = deviceInfoRepository.ipInfo()

    fun frameWorkVersion() = deviceInfoRepository.frameworkVersion()

    fun startSpeedTest(isDownload: Boolean) {
        if (isDownload) {
            speedInfoRepository.startDownload(
                userEntity?:return,
                speedProtocolType,
                downloadLiveData
            )
        }
        else {
            speedInfoRepository.startUpload(
                userEntity?:return,
                speedProtocolType,
                uploadLiveData
            )
        }
    }

    fun closeSpeedTest() = speedInfoRepository.stop()

    fun openConnection(userID: Long) = liveData(Dispatchers.IO) {
        userEntity = userDao.user(userID)

        if (userEntity == null) {
            emit(false)
            return@liveData
        }

        val jsch = JSch()
        session = jsch.getSession(
            userEntity!!.login,
            userEntity!!.serverIP,
            userEntity!!.port
        )
        session?.setPassword(userEntity!!.password)

        // Avoid asking for key confirmation.
        val properties = Properties()
        properties["StrictHostKeyChecking"] = "no"
        session?.setConfig(properties)

        try {
            session?.connect()
        } catch (e: Exception) {
            Log.e("MainViewModel", "error login", e)
            emit(false)
            return@liveData
        }

        delay(100)

        delay(100)

        emit(true)
    }
}