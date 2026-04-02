package com.example.eatik.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatik.R
import com.example.eatik.databinding.FragmentHabisBinding

class HabisFragment : Fragment(R.layout.fragment_habis) {
    private var _binding: FragmentHabisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHabisBinding.bind(view)

        binding.rvHabis.layoutManager = LinearLayoutManager(requireContext())
        
        observeViewModel()
        
        // PENTING: Perintahkan ViewModel ambil data kalau belum ada
        viewModel.getMenu()
    }

    private fun observeViewModel() {
        viewModel.listMenu.observe(viewLifecycleOwner) { menuList ->
            val listFiltered = menuList.filter { it.status.equals("Habis", ignoreCase = true) }
            val adapter = MenuAdapter(listFiltered) { item ->
                // Handle edit kalau perlu
            }
            binding.rvHabis.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
