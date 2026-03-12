package com.fitnesstracker.ui.sessions

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnesstracker.FitnessTrackerApp
import com.fitnesstracker.R
import com.fitnesstracker.databinding.FragmentSessionDetailBinding
import com.fitnesstracker.utils.DateUtils
import kotlinx.coroutines.launch

class SessionDetailFragment : Fragment() {

    private var _binding: FragmentSessionDetailBinding? = null
    private val binding get() = _binding!!

    private val args: SessionDetailFragmentArgs by navArgs()

    private val viewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private lateinit var exerciseAdapter: ExerciseDisplayAdapter

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()

        viewModel.setSessionId(args.sessionId)

        // Load session data
        lifecycleScope.launch {
            val repo = (requireActivity().application as FitnessTrackerApp).repository
            val session = repo.getSessionById(args.sessionId)
            session?.let {
                requireActivity().title = it.bodyPart
                binding.tvBodyPart.text = it.bodyPart
                binding.tvBodyPartIcon.text = bodyPartEmoji[it.bodyPart] ?: "🏋️"
                binding.tvDate.text = DateUtils.formatDate(it.date)

                if (it.durationMinutes > 0) {
                    binding.layoutDuration.visibility = View.VISIBLE
                    binding.tvDuration.text = "${it.durationMinutes} minutes"
                }

                if (it.notes.isNotBlank()) {
                    binding.tvNotes.visibility = View.VISIBLE
                    binding.tvNotes.text = it.notes
                }
            }
        }

        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            exerciseAdapter.submitList(exercises)
            binding.tvNoExercises.visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseDisplayAdapter()
        binding.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExercises.adapter = exerciseAdapter
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_session_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        findNavController().navigate(
                            SessionDetailFragmentDirections.actionSessionDetailToEditSession(
                                args.clientId, args.sessionId
                            )
                        )
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmation()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Session")
            .setMessage("Delete this training session and all its exercises?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val repo = (requireActivity().application as FitnessTrackerApp).repository
                    val session = repo.getSessionById(args.sessionId)
                    session?.let { repo.deleteSession(it) }
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
