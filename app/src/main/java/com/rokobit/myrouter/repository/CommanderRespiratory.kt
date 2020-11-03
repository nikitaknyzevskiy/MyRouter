package com.rokobit.myrouter.repository

import android.util.Log
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Session
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream

class CommanderRepository(private val onSession: () -> Session?) {

    @Synchronized
    suspend fun doCommand(command: String): String {
        val outputStream = ByteArrayOutputStream()
        Log.d("MainViewModel", "onCommand $command")

        val session = onSession.invoke()

        // Create SSH Channel.
        val sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel.outputStream = outputStream

        sshChannel.setCommand(command)
        sshChannel.connect()

        while (outputStream.size() == 0)
            delay(100)

        sshChannel.disconnect()

        return outputStream.toString()
    }

}