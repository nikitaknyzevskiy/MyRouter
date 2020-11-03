package com.rokobit.myrouter.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.rokobit.myrouter.data.DownloadSpeedInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import com.rokobit.myrouter.data.UploadSpeedInfo
import com.rokobit.myrouter.data.entity.UserEntity
import com.rokobit.myrouter.viewmodel.SpeedProtocolType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class SpeedInfoRepository(private val onSession: () -> Session?) {

    private var speedSshChanel: ChannelExec? = null

    private val downloadSpeedInfoLiveData = MutableLiveData<DownloadSpeedInfo?>()

    private var isOnRunning = false

    private var session: Session? = null

    fun startDownload(userEntity: UserEntity, protocol: SpeedProtocolType, data: MutableLiveData<DownloadSpeedInfo?>) = GlobalScope.launch(Dispatchers.IO) {
        isOnRunning = true
        speedSshChanel?.disconnect()
        downloadSpeedInfoLiveData.postValue(null)

        val jsch = JSch()
        session = jsch.getSession(
            userEntity.login,
            userEntity.serverIP,
            userEntity.port
        )
        session?.setPassword(userEntity.password)

        // Avoid asking for key confirmation.
        val properties = Properties()
        properties["StrictHostKeyChecking"] = "no"
        session?.setConfig(properties)

        session?.connect()

        val stream = ByteArrayOutputStream()

        // Create SSH Channel.
        speedSshChanel = session?.openChannel("exec") as ChannelExec

        speedSshChanel?.outputStream = stream

        speedSshChanel?.setCommand("/tool bandwidth-test address=${userEntity.speedIP} protocol=${protocol.data} direction=" + "receive")
        speedSshChanel?.connect()

        while (isOnRunning) {
            if (stream.size() == 0)
                continue
            delay(100)

            data.postValue(
                RouterInfoUtil.convertToDownloadSpeedInfo(stream.toString())
            )

            stream.reset()
        }
    }

    fun startUpload(userEntity: UserEntity, protocol: SpeedProtocolType, data: MutableLiveData<UploadSpeedInfo?>) = GlobalScope.launch(Dispatchers.IO) {
        isOnRunning = true
        speedSshChanel?.disconnect()
        downloadSpeedInfoLiveData.postValue(null)

        val jsch = JSch()
        session = jsch.getSession(
            userEntity.login,
            userEntity.serverIP,
            userEntity.port
        )
        session?.setPassword(userEntity.password)

        // Avoid asking for key confirmation.
        val properties = Properties()
        properties["StrictHostKeyChecking"] = "no"
        session?.setConfig(properties)

        session?.connect()

        val stream = ByteArrayOutputStream()

        // Create SSH Channel.
        speedSshChanel = session?.openChannel("exec") as ChannelExec

        speedSshChanel?.outputStream = stream

        speedSshChanel?.setCommand("/tool bandwidth-test address=${userEntity.speedIP} protocol=${protocol.data} direction=" + "transmit")
        speedSshChanel?.connect()

        while (isOnRunning) {
            if (stream.size() == 0)
                continue
            delay(100)

            data.postValue(
                RouterInfoUtil.convertToUploadSpeedInfo(stream.toString())
            )

            stream.reset()
        }
    }

    fun stop() {
        isOnRunning = false
        session?.disconnect()
        speedSshChanel?.disconnect()
    }

}