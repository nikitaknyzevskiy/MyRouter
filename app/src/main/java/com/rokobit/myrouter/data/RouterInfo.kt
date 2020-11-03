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

data class IpInfo(
    val address: String,
    val gateway: String,
    val dns1: String,
    val dns2: String
)

data class DownloadSpeedInfo(
    val status: String,
    val duration: String,
    val rxCurrent: String,
    val rxTenSecondAverage: String,
    val rxTotalAverage: String,
    val direction: String,
    val rxSize: String,
    val localCpu: String,
    val remoteCpu: String
)

data class UploadSpeedInfo(
    val status: String,
    val duration: String,
    val txCurrent: String,
    val txTenSecondAverage: String,
    val txTotalAverage: String,
    val direction: String,
    val txSize: String,
    val localCpu: String,
    val remoteCpu: String
)

data class RouterInfo(
    val deviceInfo: DeviceInfo,
    val isEther1Run: Boolean,
    val speed: String,
    val dnsInfo: String,
    val ether1State: String,
    var isEther1CableRun: Boolean? = null
)

data class CableInfo(
    val name: String,
    val status: String,
    val cablePairs: String
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
                it.substring(it.indexOf(':') + 1).trim().replace("\r", "").replace("\n", "")
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
        nList.forEach { it.replace("\\s".toRegex(), "") }

        /*val split = nList.map {
            if (it.isNotEmpty() && it.contains(":"))
                it.substring(it.indexOf(':') + 1).trim().replace("\r", "")
            else
                it
        }*/

        return DownloadSpeedInfo(
            status = nList.find { it.contains("status:") }?.replace("status:","")?:"".replace("\\s".toRegex(), ""),
            duration = nList.find { it.contains("duration:") }?.replace("duration:","")?:"".replace("\\s".toRegex(), ""),
            rxCurrent = nList.find { it.contains("rx-current:") }?.replace("rx-current:","")?:"",
            rxTenSecondAverage = nList.find { it.contains("rx-10-second-average:") }?.replace("rx-10-second-average:","")?:"".replace("\\s".toRegex(), ""),
            rxTotalAverage = nList.find { it.contains("rx-total-average:") }?.replace("rx-total-average:","")?:"".replace("\\s".toRegex(), ""),
            direction = nList.find { it.contains("direction:") }?.replace("direction:","")?:"".replace("\\s".toRegex(), ""),
            rxSize = nList.find { it.contains("rx-size:") }?.replace("rx-size:","")?:"".replace("\\s".toRegex(), ""),
            localCpu = nList.find { it.contains("local-cpu-load:") }?.replace("local-cpu-load:","")?:"".replace("\\s".toRegex(), ""),
            remoteCpu = nList.find { it.contains("remote-cpu-load:") }?.replace("remote-cpu-load:","")?:"".replace("\\s".toRegex(), "")
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

        return UploadSpeedInfo(
            status = nList.find { it.contains("status:") }?.replace("status:", "")?:"",
            txCurrent = nList.find { it.contains("tx-current:") }?.replace("tx-current:", "")?:"",
            txTenSecondAverage = nList.find { it.contains("tx-10-second-average:") }?.replace("tx-10-second-average:", "")?:"",
            txTotalAverage = nList.find { it.contains("tx-total-average:") }?.replace("tx-total-average:", "")?:"",
            direction = nList.find { it.contains("direction:") }?.replace("direction:", "")?:"",
            txSize = nList.find { it.contains("tx-size:") }?.replace("tx-size:", "")?:"",
            duration = nList.find { it.contains("duration:") }?.replace("duration:", "")?:"",
            localCpu = nList.find { it.contains("local-cpu-load:") }?.replace("local-cpu-load:","")?:"",
            remoteCpu = nList.find { it.contains("remote-cpu-load:") }?.replace("remote-cpu-load:","")?:""
        )
    }

    fun convertToIpInfo(data: String): IpInfo {
        return IpInfo(
            address = data.substring(data.indexOf("address=") + "address=".length,  data.indexOf(" gateway")).replace("\n", ""),
            gateway = data.substring(data.indexOf("gateway=") + "gateway=".length,  data.indexOf(" dhcp-server")).replace("\n", ""),
            dns1 = data.substring(data.indexOf("primary-dns=") + "primary-dns=".length, data.indexOf("primary-dns=") + 7 + "primary-dns=".length).replace("\n", ""),
            dns2 = data.substring(data.indexOf("secondary-dns=") + "secondary-dns=".length, data.indexOf("secondary-dns=") + 7 + "secondary-dns=".length).replace("\n", "")
        )
    }

}
