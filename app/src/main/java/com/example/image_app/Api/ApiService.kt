package com.example.image_app.Api


import com.example.image_app.DataModel.DeleteResponse
import com.example.image_app.DataModel.ImageItem
import com.example.image_app.DataModel.UploadResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ✅ POST - Image Upload
    @Multipart
    @POST("api/images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    // ✅ GET - Single Image by ID
    @GET("api/images/{id}")
    suspend fun getImage(
        @Path("id") id: String
    ): Response<ResponseBody>

    // ✅ DELETE - Image by ID
    @DELETE("api/images/{id}")
    suspend fun deleteImage(
        @Path("id") id: String
    ): Response<DeleteResponse>

    // ✅ GET - All Images List
    @GET("api/images")
    suspend fun getAllImages(): Response<List<ImageItem>>
}
