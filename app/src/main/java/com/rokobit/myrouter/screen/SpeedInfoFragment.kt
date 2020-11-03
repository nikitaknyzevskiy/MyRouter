package com.rokobit.myrouter.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.DownloadSpeedInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import com.rokobit.myrouter.data.UploadSpeedInfo
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_speed_info.*
import java.io.ByteArrayOutputStream

class SpeedInfoFragment : Fragment() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speed_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getBoolean("isDownload") == true) {
            speedinfo_title.text = "Download"
        }
        else {
            speedinfo_title.text = "Upload"
        }

        view.setOnClickListener {
            mViewModel.closeSpeedTest()
        }
    }

    override fun onStart() {
        super.onStart()

        mViewModel.uploadLiveData.observe(this.viewLifecycleOwner, uplObs)
        mViewModel.downloadLiveData.observe(this.viewLifecycleOwner, downObs)

        mViewModel.startSpeedTest(arguments?.getBoolean("isDownload")?:false)

    }

    override fun onStop() {
        mViewModel.uploadLiveData.removeObserver(uplObs)
        mViewModel.downloadLiveData.removeObserver(downObs)
        mViewModel.closeSpeedTest()
        super.onStop()
    }

    @SuppressLint("SetTextI18n")
    private val downObs = Observer<DownloadSpeedInfo?> {
        if (it == null) return@Observer
        speed_duraction.text = "${it.duration.toNumber()} s"
        speed_local_cpu.text = "${it.localCpu.toNumber()}%"
        speed_remote_cpu.text = "${it.remoteCpu.toNumber()}%"

        val speed = it.rxTotalAverage.toNumber()

        speed_speed.text = "${it.rxCurrent.toNumber()} Mbps"
        speed_average.text = "$speed Mbps"
        speed_progress.progress = it.rxCurrent.toNumber()
    }

    private val uplObs = Observer<UploadSpeedInfo?> {
        if (it == null) return@Observer
        speed_duraction.text = "${it.duration.toNumber()} s"
        speed_local_cpu.text = "${it.localCpu.toNumber()}%"
        speed_remote_cpu.text = "${it.remoteCpu.toNumber()}%"

        val speed = it.txTotalAverage.toNumber()

        speed_speed.text = "${it.txCurrent.toNumber()} Mbps"
        speed_average.text = "$speed Mbps"
        speed_progress.progress = it.txCurrent.toNumber()
    }


}

fun String.toNumber(): Float {
    if (this.isEmpty())
        return 0f

    var num = ""

    this.forEach {
        if (it.isDigit() || it == '.')
            num+=it
    }

    return num.toFloat()
}