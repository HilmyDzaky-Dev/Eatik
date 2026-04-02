package com.example.eatik.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eatik.R
import com.example.eatik.databinding.FragmentHelpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HelpFragment : Fragment(R.layout.fragment_help) {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    // 1. Tambahkan ViewModel (activityViewModels agar sharing data)
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHelpBinding.bind(view)

        // 2. Panggil observe untuk Event Listener
        observeViewModel()

        setupFaqClicks()
        setupSupportButton()
    }

    private fun observeViewModel() {
        // 3. Observe snackbarText biar kalau ada error global tetep muncul di sini
        viewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFaqClicks() {
        binding.cardFaq.setOnClickListener {
            Toast.makeText(requireContext(), "Silakan pilih pertanyaan di bawah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSupportButton() {
        binding.btnContactSupport.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hubungi CS")
                .setMessage("Anda akan diarahkan ke halaman Kontak Kami. Lanjutkan?")
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Ya") { _, _ ->
                    findNavController().navigate(R.id.menu_contact)
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}