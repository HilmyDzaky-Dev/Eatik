package com.example.eatik.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.eatik.R
import com.example.eatik.databinding.FragmentContactBinding

class ContactFragment : Fragment(R.layout.fragment_contact) {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactBinding.bind(view)

        binding.btnWhatsapp.setOnClickListener {
            // Logic Buka WhatsApp
            val url = "https://api.whatsapp.com/send?phone=628123456789" // Ganti no HP-mu
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.btnEmail.setOnClickListener {
            // Logic Buka Email
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@eatik.com")
                putExtra(Intent.EXTRA_SUBJECT, "Tanya Eatik")
            }
            startActivity(intent)
        }

        binding.btnMaps.setOnClickListener {
            val latitude = "-6.386997"
            val longitude = "106.777376"
            val label = "Eatik Kitchen"

            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}