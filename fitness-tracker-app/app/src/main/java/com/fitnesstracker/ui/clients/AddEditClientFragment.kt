package com.fitnesstracker.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitnesstracker.FitnessTrackerApp
import com.fitnesstracker.R
import com.fitnesstracker.data.database.entities.Client
import com.fitnesstracker.databinding.FragmentAddEditClientBinding
import com.google.android.material.snackbar.Snackbar

class AddEditClientFragment : Fragment() {

    private var _binding: FragmentAddEditClientBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditClientFragmentArgs by navArgs()

    private val viewModel: ClientViewModel by viewModels {
        ClientViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private var existingClient: Client? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()

        // Editing mode – load existing client
        if (args.clientId != -1L) {
            viewModel.getClientById(args.clientId).observe(viewLifecycleOwner) { client ->
                if (client != null && existingClient == null) {
                    existingClient = client
                    populateFields(client)
                }
            }
            requireActivity().title = getString(R.string.edit_client)
        } else {
            requireActivity().title = getString(R.string.add_client)
        }

        binding.btnSave.setOnClickListener { saveClient() }
    }

    private fun populateFields(client: Client) {
        binding.etName.setText(client.name)
        binding.etAge.setText(client.age.toString())
        binding.actvGender.setText(client.gender, false)
        binding.etPhone.setText(client.phone)
        binding.etEmail.setText(client.email)
        binding.actvGoal.setText(client.goal, false)
    }

    private fun setupDropdowns() {
        val genders = resources.getStringArray(R.array.genders)
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genders)
        binding.actvGender.setAdapter(genderAdapter)

        val goals = resources.getStringArray(R.array.fitness_goals)
        val goalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, goals)
        binding.actvGoal.setAdapter(goalAdapter)
    }

    private fun saveClient() {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val ageText = binding.etAge.text?.toString()?.trim() ?: ""
        val gender = binding.actvGender.text?.toString()?.trim() ?: ""

        if (name.isBlank()) {
            binding.tilName.error = "Name is required"
            return
        }
        binding.tilName.error = null

        if (ageText.isBlank()) {
            binding.tilAge.error = "Age is required"
            return
        }
        binding.tilAge.error = null

        val age = ageText.toIntOrNull()
        if (age == null || age <= 0 || age > 120) {
            binding.tilAge.error = "Enter a valid age"
            return
        }
        binding.tilAge.error = null

        val client = Client(
            id = existingClient?.id ?: 0,
            name = name,
            age = age,
            gender = gender.ifBlank { "Other" },
            phone = binding.etPhone.text?.toString()?.trim() ?: "",
            email = binding.etEmail.text?.toString()?.trim() ?: "",
            goal = binding.actvGoal.text?.toString()?.trim() ?: "",
            createdAt = existingClient?.createdAt ?: System.currentTimeMillis()
        )

        if (existingClient != null) {
            viewModel.updateClient(client)
        } else {
            viewModel.insertClient(client)
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
