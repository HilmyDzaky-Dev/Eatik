package com.example.eatik.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.eatik.R
import com.example.eatik.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setUpNavigationUI()

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashActivity) {
                binding.bottomNav.visibility = View.GONE
                binding.btnMenu.visibility = View.GONE 
            } else {
                binding.bottomNav.visibility = View.VISIBLE
                binding.btnMenu.visibility = View.VISIBLE
            }
        }

        val headerUrl = "https://i.pinimg.com/736x/a2/48/9a/a2489a9ebb904e86bdf97719f3f286a2.jpg"
        val backgroundUrl = "https://i.pinimg.com/1200x/e9/49/d8/e949d85228ebb516a52dbbe62e2a83ab.jpg"
        
        loadNavHeaderImages(headerUrl, backgroundUrl)
    }

    private fun setUpNavigationUI(){

        binding.bottomNav.setupWithNavController(navController)
        binding.navView.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                navController.navigate(item.itemId)
            }
            true
        }
    }

    private fun loadNavHeaderImages(profileUrl: String, bgUrl: String) {
        try {
            val headerView = binding.navView.getHeaderView(0)
            val ivProfile = headerView.findViewById<ImageView>(R.id.iv_profile)
            val ivBackground = headerView.findViewById<ImageView>(R.id.iv_header_bg)

            // 1. Load Foto Profile (Lingkaran)
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(ivProfile)

            // 2. Load Background Header
            Glide.with(this)
                .load(bgUrl)
                .centerCrop()
                .into(ivBackground)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
