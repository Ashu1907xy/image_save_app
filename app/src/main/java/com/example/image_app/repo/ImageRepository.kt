package com.example.image_app.repo



import com.example.image_app.Api.RetrofitInstance
import com.example.image_app.DataModel.DeleteResponse
import com.example.image_app.DataModel.ImageItem
import com.example.image_app.DataModel.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

class ImageRepository {

    private val api = RetrofitInstance.api

    // ✅ Upload Image
    suspend fun uploadImage(file: File): Result<UploadResponse> {
        return try {

            val mimeType = when (file.extension.lowercase()) {
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                else -> "image/jpeg"
            }
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "file", file.name, requestBody
            )
            val response = api.uploadImage(multipartBody)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Get All Images
    suspend fun getAllImages(): Result<List<ImageItem>> {
        return try {
            val response = api.getAllImages()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch images: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Delete Image
    suspend fun deleteImage(id: String): Result<DeleteResponse> {
        return try {
            val response = api.deleteImage(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Delete failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
