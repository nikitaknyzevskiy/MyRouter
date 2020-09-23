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
import com.rokobit.myrouter.data.DeviceInfo
import com.rokobit.myrouter.data.IpInfo
import com.rokobit.myrouter.data.RouterInfo
import com.rokobit.myrouter.databinding.FragmentDianosticBinding
import com.rokobit.myrouter.viewmodel.MainViewModel
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.deviceInfoLiveData.observe(this.viewLifecycleOwner, deviceInfoObs)
        mViewModel.isLinkRunLiveData.observe(this.viewLifecycleOwner, isLinkRunObs)
        mViewModel.rateLinkLiveData.observe(this.viewLifecycleOwner, linkRateObs)
        mViewModel.isLinkCableOkLiveData.observe(this.viewLifecycleOwner, isLinkCableOk)
        mViewModel.ipInfoLiveData.observe(this.viewLifecycleOwner, ipInfoObs)
        mViewModel.ipStateLiveData.observe(this.viewLifecycleOwner, ipStateObs)
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

    private val isLinkCableOk = Observer<Boolean> {
        binding.isLinkCableOk = it
    }

    private val ipInfoObs = Observer<IpInfo> {
        binding.ipInfo = it
    }

    private val ipStateObs = Observer<String> {
        binding.ipStatus = it
    }

    /*override fun onChanged(info: RouterInfo) {
        diagnostic_gen_pb.visibility = View.INVISIBLE

        //diagnostic_routerboard.text = info.deviceInfo.routerBoard
        diagnostic_board_name_txt.text = info.deviceInfo.boardName
        //diagnostic_model.text = info.deviceInfo.model
        //diagnostic_serial_number.text = info.deviceInfo.serialNumber
        //diagnostic_firmware_type.text = info.deviceInfo.firmwareType
        diagnostic_current_firmware.text = info.deviceInfo.currentFirmware
        //diagnostic_upgrade_firmware.text = info.deviceInfo.upgradeFirmware

        diagnostic_is_ether1_run.text = info.isEther1Run.toString()

        diagnostic_is_ether1_speed.text = info.speed

        diagnostic_is_ether1_dns.text = Html.fromHtml(info.dnsInfo, HtmlCompat.FROM_HTML_MODE_COMPACT)

        diagnostic_is_ether1_state.text = info.ether1State

        diagnostic_is_ether1_cable_run.text = info.isEther1CableRun.toString()

        diagnostic_test_speed_btn.visibility = View.VISIBLE
        diagnostic_test_speed_btn2.visibility = View.VISIBLE
    }*/

}