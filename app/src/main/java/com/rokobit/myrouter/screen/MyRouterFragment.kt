package com.rokobit.myrouter.screen

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_my_router.*

class MyRouterFragment : Fragment() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_router, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myrouter_connect_btn.setOnClickListener {
            findNavController().navigate(R.id.action_myRouterFragment_to_loginFragment)
        }

        myrouter_diagnose_btn.setOnClickListener {
            findNavController().navigate(R.id.action_myRouterFragment_to_diagnosticFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.connectionStatus.observe(this.viewLifecycleOwner, Observer {

            myrouter_pb.visibility = View.GONE

            if (it) {
                myrouter_status_img.setImageResource(R.drawable.ic_baseline_done_24)
                myrouter_status_img.imageTintList = ColorStateList.valueOf(Color.GREEN)
                myrouter_status_txt.setText(R.string.connected)
                myrouter_connect_btn.visibility = View.GONE
                myrouter_diagnose_btn.visibility = View.VISIBLE
            }
            else {
                myrouter_status_img.setImageResource(R.drawable.ic_baseline_highlight_off_24)
                myrouter_status_img.imageTintList = ColorStateList.valueOf(Color.RED)
                myrouter_status_txt.setText(R.string.disconnected)
                myrouter_connect_btn.visibility = View.VISIBLE
                myrouter_diagnose_btn.visibility = View.GONE
            }

        })
    }

}