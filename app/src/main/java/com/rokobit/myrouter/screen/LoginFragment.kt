package com.rokobit.myrouter.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.entity.UserEntity
import com.rokobit.myrouter.databinding.FragmentLoginBinding
import com.rokobit.myrouter.viewmodel.MainViewModel

class LoginFragment : Fragment() {

    private val mViewModel: MainViewModel by activityViewModels()

    private val userID: Long by lazy {
        arguments?.getLong("user_id")?:0L
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            binding.isLoading = true
            val userEntity = UserEntity(
                id = userID,
                serverIP = binding.loginIp.text.toString(),
                port = binding.loginPort.text.toString().toInt(),
                login = binding.loginUsername.text.toString(),
                password = binding.loginPassword.text.toString(),
                speedIP = binding.loginSpeedIP.text.toString()
            )
            mViewModel.save(userEntity).observe(this.viewLifecycleOwner, userSaveObs)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.user(userID).observe(this.viewLifecycleOwner, userObs)
    }

    private val userObs = Observer<UserEntity?> {
        if (it == null) return@Observer
        binding.user = it
    }

    private val userSaveObs = Observer<Long> {id ->
        binding.isLoading = false
        findNavController().navigate(R.id.action_login_to_myRouter, Bundle().apply {
            putLong("user_id", userID)
        })
    }

}