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
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_btn.setOnClickListener {

            login_pb.visibility = View.VISIBLE
            it.visibility = View.GONE

            mViewModel.login(
                login_ip.text.toString(),
                login_username.text.toString(),
                login_password.text.toString(),
                login_port.text.toString().toInt()
            ).observe(this.viewLifecycleOwner, Observer {
                login_pb.visibility = View.GONE
                login_btn.visibility = View.VISIBLE

                if (it) {
                    findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
                }

            })

        }
    }

}