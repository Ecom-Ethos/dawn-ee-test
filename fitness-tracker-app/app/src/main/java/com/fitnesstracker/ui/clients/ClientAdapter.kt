package com.fitnesstracker.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnesstracker.data.database.entities.Client
import com.fitnesstracker.databinding.ItemClientBinding

class ClientAdapter(
    private val onClientClick: (Client) -> Unit
) : ListAdapter<Client, ClientAdapter.ClientViewHolder>(DiffCallback) {

    inner class ClientViewHolder(private val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(client: Client) {
            binding.tvName.text = client.name
            binding.tvDetails.text = "${client.gender} · ${client.age} years"
            binding.tvAvatar.text = client.name.take(1).uppercase()
            if (client.goal.isNotBlank()) {
                binding.tvGoal.text = client.goal
            } else {
                binding.tvGoal.text = ""
            }
            binding.root.setOnClickListener { onClientClick(client) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Client>() {
        override fun areItemsTheSame(oldItem: Client, newItem: Client) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Client, newItem: Client) = oldItem == newItem
    }
}
