package com.example.eatik.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.eatik.R
import com.example.eatik.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        observeViewModel()
        setupProfileData()
        setupActionButtons()
    }

    private fun observeViewModel() {
        viewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupProfileData() {
        binding.tvProfileName.text = getString(R.string.admin_eatik)
        binding.tvProfileRole.text = getString(R.string.owner_restaurant)

        Glide.with(this)
            .load("https://i.pinimg.com/736x/a2/48/9a/a2489a9ebb904e86bdf97719f3f286a2.jpg")
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.ivProfilePicture)
    }

    private fun setupActionButtons() {
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur Edit Profil segera hadir!", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangePhoto.setOnClickListener {
            Toast.makeText(requireContext(), "Ganti foto profil?", Toast.LENGTH_SHORT).show()
        }

        binding.itemHelp.setOnClickListener {
            findNavController().navigate(R.id.menu_help)
        }

        binding.itemSettings.setOnClickListener {
            // Navigasi ke fragment settings sesuai request kamu
            findNavController().navigate(R.id.menu_settings)
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin keluar dari akun Eatik?")
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Ya, Keluar") { _, _ ->
                Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show()
                requireActivity().finish() 
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
