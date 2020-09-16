package com.rokobit.myrouter.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.RouterInfo
import com.rokobit.myrouter.data.RouterInfoUtil
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_dianostic.*

class DiagnosticFragment : Fragment(), Observer<RouterInfo> {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dianostic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.startDiagnostic()

        diagnostic_test_speed_btn.setOnClickListener {
            findNavController().navigate(R.id.action_diagnosticFragment_to_speedInfoFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.routerInfoLiveData.observe(this.viewLifecycleOwner, this)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.routerInfoLiveData.removeObserver(this)
    }

    override fun onChanged(info: RouterInfo) {
        diagnostic_gen_pb.visibility = View.INVISIBLE

        diagnostic_routerboard.text = info.deviceInfo.routerBoard
        diagnostic_board_name.text = info.deviceInfo.boardName
        diagnostic_model.text = info.deviceInfo.model
        diagnostic_serial_number.text = info.deviceInfo.serialNumber
        diagnostic_firmware_type.text = info.deviceInfo.firmwareType
        diagnostic_current_firmware.text = info.deviceInfo.currentFirmware
        diagnostic_upgrade_firmware.text = info.deviceInfo.upgradeFirmware

        diagnostic_is_ether1_run.text = info.isEther1Run.toString()

        diagnostic_is_ether1_speed.text = info.speed

        diagnostic_is_ether1_dns.text = info.dnsInfo

        diagnostic_is_ether1_state.text = info.ether1State

        diagnostic_is_ether1_cable_run.text = info.isEther1CableRun.toString()

        if (info.ether1State == "internet")
            diagnostic_test_speed_btn.visibility = View.VISIBLE
    }

}