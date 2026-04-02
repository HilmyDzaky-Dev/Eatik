package com.example.eatik.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.eatik.R
import com.example.eatik.databinding.FragmentSettingsBinding
import com.example.eatik.utils.Event

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Menggunakan activityViewModels agar berbagi instance dengan fragment lain
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        observeViewModel()
        setupSettingsLogic()
    }

    private fun observeViewModel() {
        // Mengamati Event untuk pesan feedback global (Toast/Snackbar)
        viewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSettingsLogic() {
        // Logic untuk Switch Notifikasi
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "diaktifkan" else "dimatikan"
            // Mengirim pesan lewat ViewModel (opsional, bisa langsung Toast juga)
            Toast.makeText(requireContext(), "Notifikasi $status", Toast.LENGTH_SHORT).show()
        }

        // Logic untuk Ganti Kata Sandi
        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.ganti_kata_sandi), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
