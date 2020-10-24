package com.rokobit.myrouter.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rokobit.myrouter.R
import com.rokobit.myrouter.ui.adapter.UserAdapter
import com.rokobit.myrouter.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.screen_user_list.*

class UserListFragment : Fragment() {

    private val mViewModel: MainViewModel by activityViewModels()

    private val mAdapter = UserAdapter {
        findNavController().navigate(R.id.action_userList_to_login, Bundle().apply {
            putLong("user_id", it.id)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList_add.setOnClickListener {
            findNavController().navigate(R.id.action_userList_to_login)
        }
        userList_recycle.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = mAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.userList.observe(this.viewLifecycleOwner, Observer {
            mAdapter.submitList(it)
        })
    }

}