package com.rokobit.myrouter.screen

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
    }

    override fun onStart() {
        super.onStart()
        mViewModel.downloadSpeedInfoLiveData.observe(this.viewLifecycleOwner, downObs)
        mViewModel.uploadSpeedInfoLiveData.observe(this.viewLifecycleOwner, uplObs)

        mViewModel.startSpeedTest(arguments?.getBoolean("isDownload")?:false)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.stopSpeedViaClose()
    }

    private val downObs = Observer<DownloadSpeedInfo> {
        speed_average.text = it.rxTotalAverage
        speed_duraction.text = it.duration
        speed_local_cpu.text = it.localCpu
        speed_remote_cpu.text = it.remoteCpu

        if (it.rxTotalAverage.isNotEmpty()) {
            var speed = ""
            it.rxTotalAverage.forEach {
                if (it.isDigit() || it == '.')
                    speed+=it
            }
            speed_progress.progress = speed.toFloat()

            speed_speed.text = "$speed Mbps"
        }
    }

    private val uplObs = Observer<UploadSpeedInfo> {
        speed_average.text = it.txTotalAverage
        speed_duraction.text = it.duration
        speed_local_cpu.text = it.localCpu
        speed_remote_cpu.text = it.remoteCpu

        if (it.txTotalAverage.isNotEmpty()) {
            var speed = ""
            it.txTotalAverage.forEach {
                if (it.isDigit() || it == '.')
                    speed+=it
            }
            speed_progress.progress = speed.toFloat()

            speed_speed.text = "$speed Mbps"
        }
    }


}