package com.fitnesstracker.ui.measurements

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitnesstracker.FitnessTrackerApp
import com.fitnesstracker.data.database.entities.Measurement
import com.fitnesstracker.databinding.FragmentAddEditMeasurementBinding
import com.fitnesstracker.utils.DateUtils
import java.util.Calendar

class AddEditMeasurementFragment : Fragment() {

    private var _binding: FragmentAddEditMeasurementBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditMeasurementFragmentArgs by navArgs()

    private val viewModel: MeasurementViewModel by viewModels {
        MeasurementViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private var selectedDateEpoch: Long = DateUtils.todayEpoch()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMeasurementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setText(DateUtils.formatDate(selectedDateEpoch))

        binding.etDate.setOnClickListener { showDatePicker() }
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener { saveMeasurement() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateEpoch }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(year, month, day)
                selectedDateEpoch = cal.timeInMillis
                binding.etDate.setText(DateUtils.formatDate(selectedDateEpoch))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun Float?.orZero() = this ?: 0f

    private fun String?.toFloatSafe() = this?.trim()?.toFloatOrNull() ?: 0f

    private fun saveMeasurement() {
        val measurement = Measurement(
            clientId = args.clientId,
            date = selectedDateEpoch,
            weight = binding.etWeight.text?.toString().toFloatSafe(),
            height = binding.etHeight.text?.toString().toFloatSafe(),
            chest = binding.etChest.text?.toString().toFloatSafe(),
            waist = binding.etWaist.text?.toString().toFloatSafe(),
            hips = binding.etHips.text?.toString().toFloatSafe(),
            leftBicep = binding.etLeftBicep.text?.toString().toFloatSafe(),
            rightBicep = binding.etRightBicep.text?.toString().toFloatSafe(),
            leftThigh = binding.etLeftThigh.text?.toString().toFloatSafe(),
            rightThigh = binding.etRightThigh.text?.toString().toFloatSafe(),
            leftCalf = binding.etLeftCalf.text?.toString().toFloatSafe(),
            rightCalf = binding.etRightCalf.text?.toString().toFloatSafe(),
            bodyFatPercent = binding.etBodyFat.text?.toString().toFloatSafe(),
            notes = binding.etNotes.text?.toString()?.trim() ?: ""
        )
        viewModel.insertMeasurement(measurement)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
