package com.example.eatik.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatik.data.MenuResponseItem
import com.example.eatik.retrofit.ApiConfig
import com.example.eatik.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel : ViewModel() {

    private val _listMenu = MutableLiveData<List<MenuResponseItem>>()
    val listMenu: LiveData<List<MenuResponseItem>> = _listMenu

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun getMenu() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getMenu()
                _listMenu.value = response
            } catch (e: Exception) {
                _snackbarText.value = Event("Gagal ambil data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveMenu(id: RequestBody?, nama: RequestBody, harga: RequestBody, deskripsi: RequestBody, kategori: RequestBody, status: RequestBody, foto: MultipartBody.Part?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().createMenu(id, nama, harga, deskripsi, kategori, status, foto)
                if (response.isSuccessful) {
                    _snackbarText.value = Event("Berhasil menyimpan data!")
                    getMenu() // Refresh list otomatis
                } else {
                    _snackbarText.value = Event("Gagal: ${response.message()}")
                }
            } catch (e: Exception) {
                _snackbarText.value = Event("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMenu(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().deleteMenu(id)
                if (response.isSuccessful) {
                    _snackbarText.value = Event("Data berhasil dihapus")
                    getMenu() // Refresh list otomatis
                } else {
                    _snackbarText.value = Event("Gagal menghapus")
                }
            } catch (e: Exception) {
                _snackbarText.value = Event("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
