package com.example.eatik.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.eatik.R
import com.example.eatik.data.MenuResponseItem
import com.example.eatik.databinding.DialogEditMenuBinding
import com.example.eatik.databinding.FragmentHomeBinding
import com.example.eatik.retrofit.ApiConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var menuAdapter: MenuAdapter
    private val listMenu = mutableListOf<MenuResponseItem>()

    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (_binding != null) {
            val nextItem = (binding.vpSlider.currentItem + 1) % 3
            binding.vpSlider.currentItem = nextItem
        }
    }

    private var selectedImageUri: Uri? = null
    private var _dialogBinding: DialogEditMenuBinding? = null

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            _dialogBinding?.ivEditFoto?.setImageURI(it)
            _dialogBinding?.layoutPlaceholder?.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupSlider()
        setupRecyclerView()
        observeViewModel()

        binding.fabAddMenu.setOnClickListener {
            showMenuDialog(null, isEdit = false)
        }

        viewModel.getMenu()
    }

    private fun setupSlider() {
        val sliderImages = listOf(
            "https://i.pinimg.com/736x/4d/89/4a/4d894ae7d925293f90b3aa7e5b3a316c.jpg",
            "https://i.pinimg.com/736x/7b/96/3d/7b963d105980b9268871dc862b8e36e0.jpg",
            "https://i.pinimg.com/1200x/b7/30/c0/b730c0ef7de427c94415813aa65ed0ef.jpg"
        )
        binding.vpSlider.adapter = SliderAdapter(sliderImages)
        binding.vpSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 2000)
            }
        })
    }

    private fun observeViewModel() {
        viewModel.listMenu.observe(viewLifecycleOwner) { menuList ->
            listMenu.clear()
            listMenu.addAll(menuList)
            menuAdapter.notifyDataSetChanged()
            // Bagian load ivPicture dihapus karena sudah diganti ViewPager2 (vpSlider)
        }

        viewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(listMenu) { item ->
            showMenuDialog(item, isEdit = true)
        }
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = menuAdapter
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = listMenu[position]
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Menu")
                    .setMessage("Yakin mau hapus ${item.nama}?")
                    .setCancelable(false)
                    .setNegativeButton("Batal") { _, _ -> menuAdapter.notifyItemChanged(position) }
                    .setPositiveButton("Hapus") { _, _ -> viewModel.deleteMenu(item.id) }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvMenu)
    }

    private fun showMenuDialog(item: MenuResponseItem?, isEdit: Boolean) {
        _dialogBinding = DialogEditMenuBinding.inflate(layoutInflater)
        selectedImageUri = null
        _dialogBinding?.apply {
            if (isEdit && item != null) {
                etEditNama.setText(item.nama)
                etEditHarga.setText(item.harga.toString())
                etEditDeskripsi.setText(item.deskripsi)
                etEditKategori.setText(item.kategori, false)
                etEditStatus.setText(item.status, false)
                Glide.with(requireContext()).load(ApiConfig.BASE_URL_IMAGE + item.foto).into(ivEditFoto)
                layoutPlaceholder.visibility = View.GONE
            }
            val categories = arrayOf("MAKANAN", "MINUMAN")
            val statuses = arrayOf("TERSEDIA", "HABIS")
            etEditKategori.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories))
            etEditStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statuses))
            btnPilihFoto.setOnClickListener { launcherGallery.launch("image/*") }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(_dialogBinding?.root).setCancelable(false).create()
        dialog.show()

        _dialogBinding?.btnDialogSimpan?.setOnClickListener {
            val nama = _dialogBinding?.etEditNama?.text.toString().trim()
            val harga = _dialogBinding?.etEditHarga?.text.toString().trim()
            val deskripsi = _dialogBinding?.etEditDeskripsi?.text.toString().trim()
            val kategori = _dialogBinding?.etEditKategori?.text.toString().trim()
            val status = _dialogBinding?.etEditStatus?.text.toString().trim()

            if (nama.isEmpty() || harga.isEmpty()) {
                Toast.makeText(requireContext(), "Nama dan Harga wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {
                saveMenu(item?.id, nama, harga, deskripsi, kategori, status, isEdit, dialog)
            }
        }
        _dialogBinding?.btnDialogBatal?.setOnClickListener { dialog.dismiss() }
    }

    private fun saveMenu(id: Int?, nama: String, harga: String, deskripsi: String, kategori: String, status: String, isEdit: Boolean, dialog: androidx.appcompat.app.AlertDialog) {
        val idBody = if (isEdit && id != null) id.toString().toPart() else null
        val namaBody = nama.toPart()
        val hargaBody = harga.toPart()
        val deskripsiBody = deskripsi.toPart()
        val kategoriBody = kategori.toPart()
        val statusBody = status.toPart()

        var imagePart: MultipartBody.Part? = null
        if (selectedImageUri != null) {
            val file = uriToFile(selectedImageUri!!, requireContext())
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            imagePart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
        } else if (!isEdit) {
            Toast.makeText(requireContext(), "Pilih foto dulu!", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.saveMenu(idBody, namaBody, hargaBody, deskripsiBody, kategoriBody, statusBody, imagePart)
        dialog.dismiss()
    }

    private fun String.toPart(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val file = File.createTempFile("temp_img", ".jpg", context.cacheDir)
        context.contentResolver.openInputStream(selectedImg)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _dialogBinding = null
    }
}
