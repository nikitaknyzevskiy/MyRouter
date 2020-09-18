package com.rokobit.myrouter.screen

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.DownloadSpeedInfo
import com.rokobit.myrouter.data.UploadSpeedInfo
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_speed_info.*

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
            speedinfo_upload_title.visibility = View.GONE
            speedinfo_upload.visibility = View.GONE
        }
        else {
            speedinfo_download_title.visibility = View.GONE
            speedinfo_download.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        mViewModel.subscribeSpeedTest(arguments?.getBoolean("isDownload")?:false)
        mViewModel.downloadSpeedInfoLiveData.observe(this.viewLifecycleOwner, downObs)
        mViewModel.uploadSpeedInfoLiveData.observe(this.viewLifecycleOwner, uplObs)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.unsubscribeSpeedTest()
        mViewModel.downloadSpeedInfoLiveData.removeObserver(downObs)
        mViewModel.uploadSpeedInfoLiveData.removeObserver(uplObs)
    }

    private val downObs = Observer<DownloadSpeedInfo> {
        val data = StringBuilder()
        data.append("<b>status:</b> ${it.status}")
        data.append("<p>")
        data.append("<b>duration:</b> ${it.duration}")
        data.append("<p>")
        data.append("<b>rxCurrent:</b> ${it.rxCurrent}")
        data.append("<p>")
        data.append("<b>rxTenSecondAverage:</b> ${it.rxTenSecondAverage}")
        data.append("<p>")
        data.append("<b>rxTotalAverage:</b> ${it.rxTotalAverage}")
        data.append("<p>")
        data.append("<b>direction:</b> ${it.direction}")
        data.append("<p>")
        data.append("<b>rxSize:</b> ${it.rxSize}")
        data.append("<p>")
        speedinfo_download.text = Html.fromHtml(data.toString(), FROM_HTML_MODE_COMPACT)
    }

    private val uplObs = Observer<UploadSpeedInfo> {
        val data = StringBuilder()
        data.append("<b>status:</b> ${it.status}")
        data.append("<p>")
        data.append("<b>duration:</b> ${it.duration}")
        data.append("<p>")
        data.append("<b>txCurrent:</b> ${it.txCurrent}")
        data.append("<p>")
        data.append("<b>txTenSecondAverage:</b> ${it.txTenSecondAverage}")
        data.append("<p>")
        data.append("<b>txTotalAverage:</b> ${it.txTotalAverage}")
        data.append("<p>")
        data.append("<b>direction:</b> ${it.direction}")
        data.append("<p>")
        data.append("<b>txSize:</b> ${it.txSize}")
        data.append("<p>")
        speedinfo_upload.text = Html.fromHtml(data.toString(), FROM_HTML_MODE_COMPACT)
    }


}