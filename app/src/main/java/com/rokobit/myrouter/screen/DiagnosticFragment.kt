package com.rokobit.myrouter.screen

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.CableInfo
import com.rokobit.myrouter.data.DeviceInfo
import com.rokobit.myrouter.data.IpInfo
import com.rokobit.myrouter.data.RouterInfo
import com.rokobit.myrouter.databinding.FragmentDianosticBinding
import com.rokobit.myrouter.viewmodel.MainViewModel
import com.rokobit.myrouter.viewmodel.SpeedProtocolType
import kotlinx.android.synthetic.main.fragment_dianostic.*

class DiagnosticFragment : Fragment() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private lateinit var binding: FragmentDianosticBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDianosticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diagnostic_test_speed_btn.setOnClickListener {
            findNavController().navigate(R.id.action_diagnosticFragment_to_speedInfoFragment, Bundle().apply {
                putBoolean("isDownload", true)
            })
        }

        diagnostic_test_speed_btn2.setOnClickListener {
            findNavController().navigate(R.id.action_diagnosticFragment_to_speedInfoFragment, Bundle().apply {
                putBoolean("isDownload", false)
            })
        }

        diagnostic_speed_radiobtn_udp.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected)
                mViewModel.speedProtocolType = SpeedProtocolType.UDP
        }

        diagnostic_speed_radiobtn_tcp.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected)
                mViewModel.speedProtocolType = SpeedProtocolType.TCP
        }

        binding.diagnosticRefreshBtn.setOnClickListener {
            clear()
            loadData()
        }

    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun clear() {
        binding.frameworkVersion = null
        binding.deviceInfo = null
        binding.isLinkRun = null
        binding.linkRate = null
        binding.isLinkCableOk = null
        binding.ipInfo = null
        binding.ipStatus = null
    }

    override fun onStop() {
        super.onStop()
        clear()
    }

    private fun loadData() {
        mViewModel.deviceInfo().observe(this.viewLifecycleOwner, deviceInfoObs)
        mViewModel.frameWorkVersion().observe(this.viewLifecycleOwner, frameWorkVersionObs)
        mViewModel.isLinkRun().observe(this.viewLifecycleOwner, isLinkRunObs)
        mViewModel.rateLink().observe(this.viewLifecycleOwner, linkRateObs)
        mViewModel.isLinkCableOk().observe(this.viewLifecycleOwner, isLinkCableOk)
        mViewModel.ipInfo().observe(this.viewLifecycleOwner, ipInfoObs)
        mViewModel.ipState().observe(this.viewLifecycleOwner, ipStateObs)
    }

    private val frameWorkVersionObs = Observer<String> {
        binding.frameworkVersion = it
    }

    private val deviceInfoObs = Observer<DeviceInfo> {
        binding.deviceInfo = it
    }

    private val isLinkRunObs = Observer<Boolean> {
        binding.isLinkRun = it
    }


    private val linkRateObs = Observer<String> {
        binding.linkRate = it
    }

    private val isLinkCableOk = Observer<CableInfo> {
        val stringBuilder = StringBuilder()
        stringBuilder.append("name: ")
        stringBuilder.append(it.name)
        stringBuilder.append("\n")
        stringBuilder.append("status: ")
        stringBuilder.append(it.status)
        stringBuilder.append("\n")
        stringBuilder.append("cable-pairs: ")
        stringBuilder.append(it.cablePairs)
        binding.isLinkCableOk = it
    }

    private val ipInfoObs = Observer<IpInfo> {
        binding.ipInfo = it
    }

    private val ipStateObs = Observer<String> {
        binding.ipStatus = it
    }

}