package com.rokobit.myrouter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class DeviceInfo(
    //routerboard:
    val routerBoard: String,
    //board-name
    val boardName: String,
    //model:
    val model: String,
    //serial-number
    val serialNumber: String,
    //firmware-type
    val firmwareType: String,
    //factory-firmware:
    val factoryFirmware: String,
    //current-firmware:
    val currentFirmware: String,
    //upgrade-firmware:
    val upgradeFirmware: String
)

data class DownloadSpeedInfo(
    val status: String,
    val duration: String,
    val rxCurrent: String,
    val rxTenSecondAverage: String,
    val rxTotalAverage: String,
    val lostPackets: String,
    val randomData: String,
    val direction: String,
    val rxSize: String,
    val connectionCount: String,
    val localCpuLoad: String,
    val remoteCpuLoad: String
)

data class UploadSpeedInfo(
    val status: String,
    val txCurrent: String,
    val txTenSecondAverage: String,
    val txTotalAverage: String,
    val randomData: String,
    val direction: String,
    val txSize: String,
    val connectionCount: String
)

data class RouterInfo(
    val deviceInfo: DeviceInfo,
    val isEther1Run: Boolean,
    val speed: String,
    val dnsInfo: String,
    val ether1State: String,
    var downloadSpeedInfo: MutableLiveData<String?> = MutableLiveData(),
    var uploadSpeedInfo: MutableLiveData<String?> = MutableLiveData(),
    var isEther1CableRun: Boolean? = null
)

object RouterInfoUtil {

    fun convertToDeviceInfo(data: String): DeviceInfo {
        val str = "routerboard: %s" + "\n" +
                          "board-name: %s" + "\n" +
                          "model: %s" + "\n" +
                          "serial-number: %s" + "\n" +
                          "firmware-type: %s" + "\n" +
                          "current-firmware: %s" + "\n" +
                          "upgrade-firmware: %s" + "\n"

        val nList = data.split("\n")

        val split = nList.map {
            if (it.isNotEmpty() && it.contains(":"))
                it.substring(it.indexOf(':') + 1).trim().replace("\r", "")
            else
                it
        }

        return DeviceInfo(
            routerBoard = split[0],
            boardName = split[1],
            model = split[2],
            serialNumber = split[3],
            firmwareType = split[4],
            factoryFirmware = split[5],
            currentFirmware = split[6],
            upgradeFirmware = split[7]
        )
    }

    fun convertToDownloadSpeedInfo(data: String): DownloadSpeedInfo {
        val str = "status: running\n" +
                "              duration: 6m53s\n" +
                "            rx-current: 97.8Mbps\n" +
                "  rx-10-second-average: 97.5Mbps\n" +
                "      rx-total-average: 97.4Mbps\n" +
                "          lost-packets: 878\n" +
                "           random-data: no\n" +
                "             direction: receive\n" +
                "               rx-size: 1500\n" +
                "      connection-count: 20\n" +
                "        local-cpu-load: 11%\n" +
                "       remote-cpu-load: 8%"

        val nList = data.split("\n")

        val split = nList.map {
            if (it.isNotEmpty() && it.contains(":"))
                it.substring(it.indexOf(':') + 1).trim().replace("\r", "")
            else
                it
        }

        return DownloadSpeedInfo(
            status = split[0],
            duration = split[1],
            rxCurrent = split[2],
            rxTenSecondAverage = split[3],
            rxTotalAverage = split[4],
            lostPackets = split[5],
            randomData = if (split.size > 6) split[6] else "no info",
            direction = if (split.size > 10) split[7] else "no info",
            rxSize = if (split.size > 8) split[8] else "no info",
            connectionCount = if (split.size > 9) split[9] else "no info",
            localCpuLoad = if (split.size > 10) split[10] else "no info",
            remoteCpuLoad = if (split.size > 11) split[11] else "no info"
        )
    }

    fun convertToUploadSpeedInfo(data: String): UploadSpeedInfo {
        val str = " status: connecting\n" +
                "            tx-current: 0bps\n" +
                "  tx-10-second-average: 0bps\n" +
                "      tx-total-average: 0bps\n" +
                "           random-data: no\n" +
                "             direction: transmit\n" +
                "               tx-size: 1500\n" +
                "      connection-count: 20"

        val nList = data.split("\n")

        val split = nList.map {
            if (it.isNotEmpty() && it.contains(":"))
                it.substring(it.indexOf(':') + 1).trim().replace("\r", "")
            else
                it
        }

        return UploadSpeedInfo(
            status = split[0],
            txCurrent = split[1],
            txTenSecondAverage = split[2],
            txTotalAverage = split[3],
            randomData = split[4],
            direction = split[5],
            txSize = split[6],
            connectionCount = split[7]
        )
    }

    fun convertToDownloadSpeedInfo(data: DownloadSpeedInfo): String {
        val str = "status: %s\n" +
                "duration: %s\n" +
                "rx-current: %s\n" +
                "rx-10-second-average: %s\n" +
                "rx-total-average: %s\n" +
                "lost-packets: %s\n" +
                "random-data: %s\n" +
                "direction: %s\n" +
                "rx-size: %s\n" +
                "connection-count: %s\n" +
                "local-cpu-load: %s\n" +
                "remote-cpu-load: %s"


        return String.format(
            str,
            data.status,
            data.duration,
            data.rxCurrent,
            data.rxTenSecondAverage,
            data.rxTotalAverage,
            data.lostPackets,
            data.randomData,
            data.direction,
            data.rxSize,
            data.connectionCount,
            data.localCpuLoad,
            data.remoteCpuLoad
        )
    }

}
