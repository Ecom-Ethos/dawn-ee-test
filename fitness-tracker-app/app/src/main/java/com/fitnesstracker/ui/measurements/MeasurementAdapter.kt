package com.fitnesstracker.ui.measurements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnesstracker.data.database.entities.Measurement
import com.fitnesstracker.databinding.ItemMeasurementBinding
import com.fitnesstracker.utils.DateUtils

class MeasurementAdapter(
    private val onDelete: (Measurement) -> Unit
) : ListAdapter<Measurement, MeasurementAdapter.MeasurementViewHolder>(DiffCallback) {

    inner class MeasurementViewHolder(private val binding: ItemMeasurementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(measurement: Measurement) {
            binding.tvDate.text = DateUtils.formatDate(measurement.date)

            fun fmt(v: Float) = if (v > 0) String.format("%.1f", v) else "—"
            fun fmtPct(v: Float) = if (v > 0) String.format("%.1f%%", v) else "—"

            binding.tvWeight.text = "${fmt(measurement.weight)} kg"
            binding.tvChest.text = "${fmt(measurement.chest)} cm"
            binding.tvWaist.text = "${fmt(measurement.waist)} cm"
            binding.tvHips.text = "${fmt(measurement.hips)} cm"

            val lb = fmt(measurement.leftBicep)
            val rb = fmt(measurement.rightBicep)
            binding.tvBicep.text = "$lb / $rb"

            binding.tvBodyFat.text = fmtPct(measurement.bodyFatPercent)

            binding.btnDelete.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Delete Measurement")
                    .setMessage("Remove measurement for ${DateUtils.formatDate(measurement.date)}?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(measurement) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val binding = ItemMeasurementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeasurementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Measurement>() {
        override fun areItemsTheSame(oldItem: Measurement, newItem: Measurement) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Measurement, newItem: Measurement) = oldItem == newItem
    }
}
