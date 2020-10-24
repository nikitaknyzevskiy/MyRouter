package com.rokobit.myrouter.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rokobit.myrouter.R
import com.rokobit.myrouter.data.entity.UserEntity

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        val layoutID = R.layout.viewholder_user
    }

    private val nameTxt: TextView = itemView.findViewById(R.id.item_user_name)

    @SuppressLint("SetTextI18n")
    fun bind(userEntity: UserEntity) {
        nameTxt.text = "${userEntity.login}@${userEntity.serverIP}:${userEntity.port}"
    }

}

class UserAdapter(private val onClick: (UserEntity) -> Unit) : RecyclerView.Adapter<UserViewHolder>() {

    private val data = arrayListOf<UserEntity>()

    fun submitList(list: List<UserEntity>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(UserViewHolder.layoutID, parent, false))

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder
            .bind(
                data[position]
            )
        holder.itemView.setOnClickListener {
            onClick.invoke(data[position])
        }
    }

    override fun getItemCount(): Int = data.size

}