package com.rokobit.myrouter.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.viewmodel.MainViewModel

class MenuFragment : Fragment(), View.OnClickListener {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var i = 0
        while (i < (view as LinearLayout).childCount - 1) {
            val view = (view as LinearLayout).getChildAt(i)
            view.setOnClickListener(this)
            i++
        }

    }

    override fun onClick(btn: View) {
        val command = when (btn.tag as String) {
            "1" -> "/system routerboard print"
            "2" -> ":put [/interface ethernet get ether1 running]"
            "3" -> ":put [/interface ethernet get ether1 speed]"
            "4" -> "/interface ethernet cable-test ether1"
            "5" -> "ip dhcp-client print detail"
            "6" -> ":put [/interface detect-internet state get ether1 state]"
            "7" -> "/tool bandwidth-test address=81.25.234.40 direction=receive"
            else -> "/tool bandwidth-test address=81.25.234.40 direction=transmit"
        }
        findNavController().navigate(R.id.action_menuFragment_to_commandFragment, Bundle().apply {
            putString("main_command", command)
        })
    }

}