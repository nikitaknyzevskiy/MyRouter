package com.rokobit.myrouter.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rokobit.myrouter.R
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

    override fun onStart() {
        super.onStart()
        mViewModel.subscribeRouterSpeed()
        mViewModel.downloadSpeedInfoLiveData.observe(this.viewLifecycleOwner, downObs)
        mViewModel.uploadSpeedInfoLiveData.observe(this.viewLifecycleOwner, uplObs)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.unsubscribeRouterSpeed()
        mViewModel.downloadSpeedInfoLiveData.removeObserver(downObs)
        mViewModel.uploadSpeedInfoLiveData.removeObserver(uplObs)
    }

    private val downObs = Observer<String> {
        speedinfo_download.text = it
    }

    private val uplObs = Observer<String> {
        speedinfo_upload.text = it
    }


}