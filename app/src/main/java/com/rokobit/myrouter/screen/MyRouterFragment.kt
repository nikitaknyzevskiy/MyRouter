package com.rokobit.myrouter.screen

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_my_router.*

class MyRouterFragment : Fragment() {

    private val mViewModel: MainViewModel by activityViewModels()

    private val userID: Long by lazy {
        arguments?.getLong("user_id")?:0L
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

        myrouter_diagnose_btn.setOnClickListener {
            findNavController().navigate(R.id.action_myRouterFragment_to_diagnosticFragment)
        }

        mViewModel.openConnection(userID).observe(this.viewLifecycleOwner, Observer {
            myrouter_pb.visibility = View.GONE

            if (it) {
                myrouter_status_img.setImageResource(R.drawable.ic_baseline_done_24)
                myrouter_status_img.imageTintList = ColorStateList.valueOf(Color.GREEN)
                myrouter_status_txt.setText(R.string.connected)
                myrouter_diagnose_btn.visibility = View.VISIBLE
            }
            else {
                myrouter_status_img.setImageResource(R.drawable.ic_baseline_highlight_off_24)
                myrouter_status_img.imageTintList = ColorStateList.valueOf(Color.RED)
                myrouter_status_txt.setText(R.string.disconnected)
                myrouter_diagnose_btn.visibility = View.GONE
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}