package com.fitnesstracker.ui.clients

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
import com.fitnesstracker.databinding.FragmentClientDetailBinding
import com.fitnesstracker.ui.measurements.MeasurementAdapter
import com.fitnesstracker.ui.measurements.MeasurementViewModel
import com.fitnesstracker.ui.measurements.MeasurementViewModelFactory
import com.fitnesstracker.ui.sessions.SessionAdapter
import com.fitnesstracker.ui.sessions.SessionViewModel
import com.fitnesstracker.ui.sessions.SessionViewModelFactory
import com.fitnesstracker.utils.DateUtils
import kotlinx.coroutines.launch

class ClientDetailFragment : Fragment() {

    private var _binding: FragmentClientDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ClientDetailFragmentArgs by navArgs()

    private val clientViewModel: ClientViewModel by viewModels {
        ClientViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private val measurementViewModel: MeasurementViewModel by viewModels {
        MeasurementViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var measurementAdapter: MeasurementAdapter
    private var currentTab = 0   // 0 = Sessions, 1 = Measurements

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerViews()
        setupTabs()
        setupObservers()

        sessionViewModel.setClientId(args.clientId)
        measurementViewModel.setClientId(args.clientId)

        binding.fabAdd.setOnClickListener { onFabClick() }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_client_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        findNavController().navigate(
                            ClientDetailFragmentDirections.actionClientDetailToEditClient(args.clientId)
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

    private fun setupRecyclerViews() {
        sessionAdapter = SessionAdapter { session ->
            findNavController().navigate(
                ClientDetailFragmentDirections.actionClientDetailToSessionDetail(
                    session.id, args.clientId
                )
            )
        }
        binding.rvSessions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessions.adapter = sessionAdapter

        measurementAdapter = MeasurementAdapter { measurement ->
            measurementViewModel.deleteMeasurement(measurement)
        }
        binding.rvMeasurements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeasurements.adapter = measurementAdapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                switchTab(currentTab)
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun switchTab(tab: Int) {
        if (tab == 0) {
            binding.rvSessions.visibility = View.VISIBLE
            binding.rvMeasurements.visibility = View.GONE
            refreshSessionsEmpty()
        } else {
            binding.rvSessions.visibility = View.GONE
            binding.rvMeasurements.visibility = View.VISIBLE
            refreshMeasurementsEmpty()
        }
    }

    private fun refreshSessionsEmpty() {
        val isEmpty = sessionAdapter.currentList.isEmpty()
        binding.layoutEmptySessions.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.layoutEmptyMeasurements.visibility = View.GONE
    }

    private fun refreshMeasurementsEmpty() {
        val isEmpty = measurementAdapter.currentList.isEmpty()
        binding.layoutEmptyMeasurements.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.layoutEmptySessions.visibility = View.GONE
    }

    private fun setupObservers() {
        val repo = (requireActivity().application as FitnessTrackerApp).repository

        clientViewModel.getClientById(args.clientId).observe(viewLifecycleOwner) { client ->
            if (client == null) return@observe
            requireActivity().title = client.name
            binding.tvHeaderName.text = client.name
            binding.tvHeaderDetails.text = "${client.gender} · ${client.age} years old"
            binding.tvHeaderAvatar.text = client.name.take(1).uppercase()
            binding.tvHeaderGoal.text = client.goal.ifBlank { "General Fitness" }
        }

        sessionViewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            sessionAdapter.submitList(sessions)
            if (currentTab == 0) refreshSessionsEmpty()
        }

        sessionViewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            binding.tvSessionCount.text = sessions.size.toString()
        }

        measurementViewModel.measurements.observe(viewLifecycleOwner) { measurements ->
            measurementAdapter.submitList(measurements)
            val latestWeight = measurements.firstOrNull()?.weight
            if (latestWeight != null && latestWeight > 0) {
                binding.tvLatestWeight.text = String.format("%.1f", latestWeight)
            } else {
                binding.tvLatestWeight.text = "—"
            }
            if (currentTab == 1) refreshMeasurementsEmpty()
        }
    }

    private fun onFabClick() {
        if (currentTab == 0) {
            findNavController().navigate(
                ClientDetailFragmentDirections.actionClientDetailToAddSession(args.clientId)
            )
        } else {
            findNavController().navigate(
                ClientDetailFragmentDirections.actionClientDetailToAddMeasurement(args.clientId)
            )
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Client")
            .setMessage(getString(R.string.delete_client_confirm))
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val repo = (requireActivity().application as FitnessTrackerApp).repository
                    val client = repo.getClientByIdSync(args.clientId)
                    client?.let { repo.deleteClient(it) }
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
