package com.rokobit.myrouter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.rokobit.myrouter.data.RouterInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.*

class MainViewModel : ViewModel() {

    val connectionStatus = MutableLiveData<Boolean>(false)

    val routerInfoLiveData = MutableLiveData<RouterInfo>()

    private var canStreamSpeedInfo = false

    val downloadSpeedInfoLiveData = MutableLiveData<String>()
    val uploadSpeedInfoLiveData = MutableLiveData<String>()

    private var sshChannel: ChannelExec? = null
    private var session: Session? = null

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

    fun startDiagnostic() = GlobalScope.launch(Dispatchers.IO) {
        val deviceInfo = deviceInfo()
        delay(1000)
        val ether1Running = isEther1Running()
        delay(1000)
        val ether1Speed = ether1Speed()
        delay(1000)
        val dnsInfo = dnsInfo()
        delay(1000)
        val ether1State = ether1State()

        delay(1000)

        val routerInfo = RouterInfo(
            deviceInfo = deviceInfo,
            isEther1Run = ether1Running,
            speed = ether1Speed,
            dnsInfo = dnsInfo,
            ether1State = ether1State
        )

        if (!ether1Running)
            routerInfo.isEther1CableRun = isEther1CableRun()

        routerInfoLiveData.postValue(routerInfo)
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("MainViewModel", "download info", exception)
    }

    fun subscribeRouterSpeed() = GlobalScope.launch(Dispatchers.IO + handler) {
        canStreamSpeedInfo = true
        downloadInfo()
        //uploadInfo()
    }

    fun unsubscribeRouterSpeed() {
        canStreamSpeedInfo = false
    }

    private suspend fun doCommand(command: String): String {
        val outputStream = ByteArrayOutputStream()

        // Create SSH Channel.
        sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = outputStream

        sshChannel?.setCommand(command)
        sshChannel?.connect()

        while (outputStream.size() == 0)
            delay(100)

        return outputStream.toString()
    }

    private suspend fun deviceInfo() =
        RouterInfoUtil.convertToDeviceInfo(doCommand("/system routerboard print"))

    private suspend fun isEther1Running() =
        doCommand(":put [/interface ethernet get ether1 running]").toBoolean()

    private suspend fun ether1Speed() =
        doCommand(":put [/interface ethernet get ether1 speed]")

    private suspend fun dnsInfo() =
        doCommand("ip dhcp-client print detail")

    private suspend fun ether1State() =
        doCommand(":put [/interface detect-internet state get ether1 state]")

    private suspend fun isEther1CableRun() =
        doCommand("/interface ethernet cable-test ether1").toBoolean()

    private suspend fun downloadInfo() {
        val stream = ByteArrayOutputStream()

        // Create SSH Channel.
        sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = stream

        sshChannel?.setCommand("/tool bandwidth-test address=81.25.234.40 direction=receive")
        sshChannel?.connect()

        while (canStreamSpeedInfo) {
            if (stream.size() > 0) {
                downloadSpeedInfoLiveData.postValue(stream.toString())
                stream.reset()
            }
            delay(100)
        }
    }

    private suspend fun uploadInfo() {
        val stream = ByteArrayOutputStream()

        // Create SSH Channel.
        sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = stream

        sshChannel?.setCommand("/tool bandwidth-test address=81.25.234.40 direction=transmit")
        sshChannel?.connect()

        while (canStreamSpeedInfo) {
            if (stream.size() > 0) {
                uploadSpeedInfoLiveData.postValue(stream.toString())
                stream.reset()
            }
            delay(100)
        }
    }
}