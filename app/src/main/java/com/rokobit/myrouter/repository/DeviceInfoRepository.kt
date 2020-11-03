package com.rokobit.myrouter.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Session
import com.rokobit.myrouter.data.CableInfo
import com.rokobit.myrouter.data.IpInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream

class DeviceInfoRepository(private val onSession: () -> Session?) {

    private val commanderRepository = CommanderRepository(onSession)

    fun deviceInfo() = liveData(Dispatchers.IO) {
        val data = commanderRepository.doCommand("/system routerboard print")
        val convertToDeviceInfo = RouterInfoUtil.convertToDeviceInfo(data)

        emit(convertToDeviceInfo)
    }

    fun frameworkVersion() = liveData(Dispatchers.IO) {
        emit(
            commanderRepository.doCommand(":put [/system package update get installed-version]")
                .replace("\n", "")
                .replace("\\s".toRegex(), "")
        )
    }

    fun isLinkRun() = liveData(Dispatchers.IO) {
        emit(
            commanderRepository.doCommand(":put [/interface ethernet get ether1 running]").contains("true")
        )
    }

    fun rateLink() = liveData(Dispatchers.IO) {
        val isLinkRun = commanderRepository.doCommand(":put [/interface ethernet get ether1 running]").contains("true")
        if (!isLinkRun)
            emit("NO")
        else
            emit(
                commanderRepository.doCommand(":put [/interface ethernet get ether1 speed]").replace("\n", "")
            )
    }

    fun isLinkCableOk() = liveData(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()

        val session = onSession.invoke()

        // Create SSH Channel.
        val sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel.outputStream = outputStream

        sshChannel.setCommand("/interface ethernet cable-test ether1")
        sshChannel.connect()

        while (outputStream.size() == 0)
            delay(100)

        //outputStream.reset()

        delay(500)

        val output = outputStream.toString()

        val data = CableInfo(
            name = output.value("name")?:"",
            status = output.value("status")?:"",
            cablePairs = output.value("cable-pairs")?:""
        )

        sshChannel.disconnect()

        emit(
            data
        )
    }

    fun ipState() = liveData(Dispatchers.IO) {
        emit(
            commanderRepository.doCommand(":put [/interface detect-internet state get ether1 state]").replace("\n", "")
        )
    }

    fun ipInfo() = liveData(Dispatchers.IO) {
        val address = commanderRepository.doCommand(":put [/ip dhcp-client get 0 address]").replace("\n", "").replace("\r", "")
        val gatewat = commanderRepository.doCommand(":put [/ip dhcp-client get 0 gateway]").replace("\n", "").replace("\r", "")
        val primeDns = commanderRepository.doCommand(":put [/ip dhcp-client get 0 primary-dns]").replace("\n", "").replace("\r", "")
        val secoundDns = commanderRepository.doCommand(":put [/ip dhcp-client get 0 secondary-dns]").replace("\n", "").replace("\r", "")

        emit(IpInfo(
            address = address,
            gateway = gatewat,
            dns1 = primeDns,
            dns2 = secoundDns
        ))
    }
}

fun String.value(key: String) : String? {
    val keys = this.split("\n")
    keys.forEach { it.replace("\\s".toRegex(), "") }

    return keys.find { it.contains(key) }
        ?.replace(key, "")
        ?.replace(":", "")
        ?.replace("\\s".toRegex(), "")
}