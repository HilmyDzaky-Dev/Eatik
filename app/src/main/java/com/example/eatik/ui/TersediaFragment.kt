package com.example.eatik.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatik.R
import com.example.eatik.databinding.FragmentTersediaBinding

class TersediaFragment : Fragment(R.layout.fragment_tersedia) {
    private var _binding: FragmentTersediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTersediaBinding.bind(view)

        binding.rvTersedia.layoutManager = LinearLayoutManager(requireContext())
        
        observeViewModel()
        
        // PENTING: Perintahkan ViewModel ambil data kalau belum ada
        viewModel.getMenu()
    }

    private fun observeViewModel() {
        viewModel.listMenu.observe(viewLifecycleOwner) { menuList ->
            val listFiltered = menuList.filter { it.status.equals("Tersedia", ignoreCase = true) }
            val adapter = MenuAdapter(listFiltered) { item ->
                // Handle edit kalau perlu
            }
            binding.rvTersedia.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
