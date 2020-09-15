package com.rokobit.myrouter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.util.*

class MainViewModel : ViewModel() {

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
                emit(false)
                return@liveData
            }

            delay(100)

            delay(100)

            emit(true)
        }

    fun sendCommand(command: String) = liveData(Dispatchers.IO) {
        // Execute command.
        //sshChannel?.setCommand("clear")
        //sshChannel?.connect()

        val outputStream = ByteArrayOutputStream()

        // Create SSH Channel.
        sshChannel = session?.openChannel("exec") as ChannelExec
        sshChannel?.outputStream = outputStream

        delay(
            100
        )

        sshChannel?.setCommand(command)
        sshChannel?.connect()


        delay(1000)

        emit(outputStream.toString())
    }

}