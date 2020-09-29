package com.rokobit.myrouter.viewmodel

import android.text.Html
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.rokobit.myrouter.data.*
import com.rokobit.myrouter.viewmodel.MainViewModelUtil.commandList
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.*

object MainViewModelUtil {
    val commandList = arrayListOf<String>()
}

class MainViewModel : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("MainViewModel", "download info", exception)
    }

    val connectionStatus = MutableLiveData<Boolean>(false)

    val deviceInfoLiveData = liveData(Dispatchers.IO) {
        val data = doCommand("/system routerboard print")
        val convertToDeviceInfo = RouterInfoUtil.convertToDeviceInfo(data)
        emit(convertToDeviceInfo)
    }
    val isLinkRunLiveData = liveData(Dispatchers.IO) {
        emit(
            doCommand(":put [/interface ethernet get ether1 running]").contains("true")
        )
    }
    val rateLinkLiveData = liveData(Dispatchers.IO) {
        emit(
            doCommand(":put [/interface ethernet get ether1 speed]").replace("\n", "")
        )
    }
    val isLinkCableOkLiveData = liveData(Dispatchers.IO) {
        emit(
            doCommand("/interface ethernet cable-test ether1").contains("true")
        )
    }
    val ipStateLiveData = liveData(Dispatchers.IO) {
        emit(
            doCommand(":put [/interface detect-internet state get ether1 state]").replace("\n", "")
        )
    }
    val ipInfoLiveData = liveData(Dispatchers.IO) {
        emit(
            RouterInfoUtil.convertToIpInfo(doCommand("ip dhcp-client print detail"))
        )
    }

    val downloadSpeedInfoLiveData = MutableLiveData<DownloadSpeedInfo>()
    val uploadSpeedInfoLiveData = MutableLiveData<UploadSpeedInfo>()

    private var session: Session? = null

    private var speedSshChanel: ChannelExec? = null

    fun login(serverIP: String, login: String, password: String, port: Int) =
        liveData(Dispatchers.IO) {
            val jsch = JSch()
            session = jsch.getSession(login, serverIP, port)
            session?.setPassword(password)

            // Avoid asking for key confirmation.
            val properties = Properties()
            properties["StrictHostKeyChecking"] = "no"
            session?.setConfig(properties)

            try {
                session?.connect()
            } catch (e: Exception) {
                Log.e("MainViewModel", "error login", e)
                connectionStatus.postValue(false)
                emit(false)
                return@liveData
            }

            delay(100)

            delay(100)

            connectionStatus.postValue(true)

            emit(true)
        }

    fun sendCommand(command: String) = liveData(Dispatchers.IO) {
        val result = doCommand(command)
        emit(result)
    }

    @Synchronized
    private suspend fun doCommand(command: String): String {
        val outputStream = ByteArrayOutputStream()
        Log.d("MainViewModel", "onCommand $command")

        // Create SSH Channel.
        val sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = outputStream

        sshChannel?.setCommand(command)
        sshChannel?.connect()

        while (outputStream.size() == 0)
            delay(100)

        Log.d("MainViewModel", "command $command result ${outputStream.toString()}")

        sshChannel?.disconnect()

        return outputStream.toString()
    }

    private var isSpeedRun = false

    fun startSpeedTest(isDownload: Boolean) = GlobalScope.launch(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()

        isSpeedRun = true

        // Create SSH Channel.
        speedSshChanel?.disconnect()
        speedSshChanel = session?.openChannel("exec") as ChannelExec
        speedSshChanel?.outputStream = stream

        speedSshChanel?.setCommand("/tool bandwidth-test address=81.25.234.40 direction=" + if (isDownload) "receive" else "transmit")
        speedSshChanel?.connect()

        while (isSpeedRun) {
            if (stream.size() == 0)
                continue
            delay(100)

            if (isDownload)
                downloadSpeedInfoLiveData.postValue(
                    RouterInfoUtil.convertToDownloadSpeedInfo(stream.toString())
                )
            else
                uploadSpeedInfoLiveData.postValue(
                    RouterInfoUtil.convertToUploadSpeedInfo(stream.toString())
                )

            stream.reset()
        }
    }

    fun stopSpeedViaQ() = GlobalScope.launch(Dispatchers.IO) {
        isSpeedRun = false
        speedSshChanel?.setCommand("Q")
        speedSshChanel?.connect()
    }

    fun stopSpeedViaClose() = GlobalScope.launch(Dispatchers.IO) {
        isSpeedRun = false
        speedSshChanel?.disconnect()
    }

    /*private suspend fun uploadInfo() {
        val stream = ByteArrayOutputStream()

        // Create SSH Channel.
        val sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = stream

        sshChannel?.setCommand("/tool bandwidth-test address=81.25.234.40 direction=transmit")
        sshChannel?.connect()

        while (canStreamSpeedInfo) {
            if (stream.size() > 0) {
                uploadSpeedInfoLiveData.postValue(RouterInfoUtil.convertToUploadSpeedInfo(stream.toString()))
                stream.reset()
            }
            delay(100)
        }
    }*/
}