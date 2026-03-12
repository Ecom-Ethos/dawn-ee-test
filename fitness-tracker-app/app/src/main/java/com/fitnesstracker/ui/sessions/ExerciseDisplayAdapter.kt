package com.fitnesstracker.ui.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnesstracker.data.database.entities.Exercise
import com.fitnesstracker.databinding.ItemExerciseDisplayBinding

class ExerciseDisplayAdapter :
    ListAdapter<Exercise, ExerciseDisplayAdapter.ExerciseViewHolder>(DiffCallback) {

    inner class ExerciseViewHolder(private val binding: ItemExerciseDisplayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: Exercise, position: Int) {
            binding.tvNumber.text = (position + 1).toString()
            binding.tvExerciseName.text = exercise.name
            binding.tvSetsReps.text = "${exercise.sets} sets × ${exercise.reps} reps"
            binding.tvWeight.text = if (exercise.weightKg > 0) {
                "${exercise.weightKg} kg"
            } else {
                "Bodyweight"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseDisplayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise) = oldItem == newItem
    }
}
