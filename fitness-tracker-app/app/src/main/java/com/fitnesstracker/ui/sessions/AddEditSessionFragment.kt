package com.fitnesstracker.ui.sessions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitnesstracker.FitnessTrackerApp
import com.fitnesstracker.R
import com.fitnesstracker.data.database.entities.Exercise
import com.fitnesstracker.data.database.entities.Session
import com.fitnesstracker.databinding.FragmentAddEditSessionBinding
import com.fitnesstracker.databinding.ItemExerciseInputBinding
import com.fitnesstracker.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditSessionFragment : Fragment() {

    private var _binding: FragmentAddEditSessionBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditSessionFragmentArgs by navArgs()

    private val viewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private var selectedDateEpoch: Long = DateUtils.todayEpoch()
    private val exerciseBindings = mutableListOf<ItemExerciseInputBinding>()
    private var existingSession: Session? = null
    private var exerciseCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setText(DateUtils.formatDate(selectedDateEpoch))
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnAddExercise.setOnClickListener { addExerciseRow() }
        binding.btnSave.setOnClickListener { saveSession() }

        // Pre-select Chest chip
        binding.chipChest.isChecked = true

        // Load existing session for editing
        if (args.sessionId != -1L) {
            requireActivity().title = getString(R.string.edit_session)
            loadExistingSession(args.sessionId)
        } else {
            requireActivity().title = getString(R.string.add_session)
            addExerciseRow()  // Start with one exercise row
        }
    }

    private fun loadExistingSession(sessionId: Long) {
        lifecycleScope.launch {
            val repo = (requireActivity().application as FitnessTrackerApp).repository
            val session = repo.getSessionById(sessionId)
            if (session != null) {
                existingSession = session
                selectedDateEpoch = session.date
                binding.etDate.setText(DateUtils.formatDate(session.date))
                selectBodyPartChip(session.bodyPart)
                if (session.durationMinutes > 0) {
                    binding.etDuration.setText(session.durationMinutes.toString())
                }
                binding.etNotes.setText(session.notes)

                val exercises = repo.getExercisesForSessionSync(sessionId)
                if (exercises.isEmpty()) {
                    addExerciseRow()
                } else {
                    exercises.forEach { addExerciseRow(it) }
                }
            }
        }
    }

    private fun selectBodyPartChip(bodyPart: String) {
        val chipMap = mapOf(
            "Chest" to binding.chipChest,
            "Back" to binding.chipBack,
            "Legs" to binding.chipLegs,
            "Shoulders" to binding.chipShoulders,
            "Arms" to binding.chipArms,
            "Core" to binding.chipCore,
            "Full Body" to binding.chipFullBody,
            "Cardio" to binding.chipCardio
        )
        chipMap[bodyPart]?.isChecked = true
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

    private fun addExerciseRow(exercise: Exercise? = null) {
        exerciseCounter++
        val exBinding = ItemExerciseInputBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.containerExercises,
            true
        )
        exBinding.tvExerciseNumber.text = exerciseCounter.toString()

        // Populate if editing
        exercise?.let {
            exBinding.actvExerciseName.setText(it.name)
            exBinding.etSets.setText(it.sets.toString())
            exBinding.etReps.setText(it.reps.toString())
            if (it.weightKg > 0) exBinding.etWeight.setText(it.weightKg.toString())
        }

        // Autocomplete for exercise names
        lifecycleScope.launch {
            val names = viewModel.getExerciseNamesForAutocomplete("")
            if (names.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    names
                )
                exBinding.actvExerciseName.setAdapter(adapter)
            }
        }

        exBinding.btnRemoveExercise.setOnClickListener {
            binding.containerExercises.removeView(exBinding.root)
            exerciseBindings.remove(exBinding)
            updateNoExercisesVisibility()
        }

        exerciseBindings.add(exBinding)
        updateNoExercisesVisibility()
    }

    private fun updateNoExercisesVisibility() {
        binding.tvNoExercises.visibility =
            if (exerciseBindings.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun getSelectedBodyPart(): String {
        val checkedId = binding.chipGroupBodyPart.checkedChipId
        return when (checkedId) {
            R.id.chipChest -> "Chest"
            R.id.chipBack -> "Back"
            R.id.chipLegs -> "Legs"
            R.id.chipShoulders -> "Shoulders"
            R.id.chipArms -> "Arms"
            R.id.chipCore -> "Core"
            R.id.chipFullBody -> "Full Body"
            R.id.chipCardio -> "Cardio"
            else -> "Full Body"
        }
    }

    private fun collectExercises(sessionId: Long = 0): List<Exercise> {
        return exerciseBindings.mapNotNull { b ->
            val name = b.actvExerciseName.text?.toString()?.trim() ?: ""
            if (name.isBlank()) return@mapNotNull null
            Exercise(
                sessionId = sessionId,
                name = name,
                sets = b.etSets.text?.toString()?.toIntOrNull() ?: 1,
                reps = b.etReps.text?.toString()?.toIntOrNull() ?: 1,
                weightKg = b.etWeight.text?.toString()?.toFloatOrNull() ?: 0f
            )
        }
    }

    private fun saveSession() {
        val duration = binding.etDuration.text?.toString()?.toIntOrNull() ?: 0
        val session = Session(
            id = existingSession?.id ?: 0,
            clientId = args.clientId,
            date = selectedDateEpoch,
            bodyPart = getSelectedBodyPart(),
            durationMinutes = duration,
            notes = binding.etNotes.text?.toString()?.trim() ?: ""
        )
        val exercises = collectExercises()

        if (existingSession != null) {
            viewModel.updateSession(session, exercises)
        } else {
            viewModel.insertSession(session, exercises)
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exerciseBindings.clear()
        _binding = null
    }
}
