package com.fitnesstracker.ui.clients

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnesstracker.FitnessTrackerApp
import com.fitnesstracker.databinding.FragmentClientListBinding

class ClientListFragment : Fragment() {

    private var _binding: FragmentClientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientViewModel by viewModels {
        ClientViewModelFactory((requireActivity().application as FitnessTrackerApp).repository)
    }

    private lateinit var adapter: ClientAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSearch()

        binding.fabAddClient.setOnClickListener {
            findNavController().navigate(
                ClientListFragmentDirections.actionClientListToAddClient()
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = ClientAdapter { client ->
            findNavController().navigate(
                ClientListFragmentDirections.actionClientListToClientDetail(client.id)
            )
        }
        binding.rvClients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClients.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.clients.observe(viewLifecycleOwner) { clients ->
            adapter.submitList(clients)
            if (clients.isEmpty()) {
                binding.rvClients.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.rvClients.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
        }

        viewModel.clientCount.observe(viewLifecycleOwner) { count ->
            binding.tvClientCount.text = "$count client${if (count != 1) "s" else ""}"
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
