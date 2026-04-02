package com.example.eatik.retrofit

import com.example.eatik.data.MenuResponse
import com.example.eatik.data.MenuResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("menu")
    suspend fun getMenu(): List<MenuResponseItem>

    @Multipart
    @POST("menu")
    suspend fun createMenu(
        @Part("id") id: RequestBody?, // Nullable untuk Tambah, Isi untuk Edit
        @Part("nama") nama: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("status") status: RequestBody,
        @Part foto: MultipartBody.Part? // Nullable kalau user gak ganti foto pas edit
    ): Response<MenuResponse> // Sesuaikan dengan class Response kamu

    // 3. Fungsi untuk HAPUS data berdasarkan ID
    @DELETE("menu/{id}")
    suspend fun deleteMenu(
        @Path("id") id: Int
    ): Response<Unit>
}