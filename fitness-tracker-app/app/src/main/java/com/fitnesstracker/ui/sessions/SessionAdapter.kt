package com.fitnesstracker.ui.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnesstracker.data.database.entities.Session
import com.fitnesstracker.databinding.ItemSessionBinding
import com.fitnesstracker.utils.DateUtils

class SessionAdapter(
    private val onSessionClick: (Session) -> Unit
) : ListAdapter<Session, SessionAdapter.SessionViewHolder>(DiffCallback) {

    private val bodyPartEmoji = mapOf(
        "Chest" to "💪",
        "Back" to "🔙",
        "Legs" to "🦵",
        "Shoulders" to "🏋️",
        "Arms" to "💪",
        "Core" to "🎯",
        "Full Body" to "🔥",
        "Cardio" to "🏃"
    )

    inner class SessionViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {
            binding.tvBodyPart.text = session.bodyPart
            binding.tvDate.text = DateUtils.formatDateShort(session.date)
            binding.tvBodyPartIcon.text = bodyPartEmoji[session.bodyPart] ?: "🏋️"

            if (session.durationMinutes > 0) {
                binding.tvDuration.text = "${session.durationMinutes} min"
            } else {
                binding.tvDuration.text = ""
            }

            // Exercise count shown via tag (set from SessionDetailFragment or we show placeholder)
            binding.tvExerciseCount.text = "Tap to view exercises"

            binding.root.setOnClickListener { onSessionClick(session) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Session, newItem: Session) = oldItem == newItem
    }
}
