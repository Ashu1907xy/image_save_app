package com.example.image_app.viewmodel


import android.net.Uri
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.image_app.DataModel.ImageItem
import com.example.image_app.repo.ImageRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ImageViewModel : ViewModel() {

    private val repository = ImageRepository()

    // ✅ UI States
    private val _images = MutableStateFlow<List<ImageItem>>(emptyList())
    val images: StateFlow<List<ImageItem>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ✅ Load All Images
    fun loadAllImages() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllImages()
            result.onSuccess { list ->
                _images.value = list
            }.onFailure { error ->
                _message.value = "Error: ${error.message}"
            }
            _isLoading.value = false
        }
    }

    // ✅ Upload Image
    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val file = uriToFile(context, uri)
            if (file != null) {
                val result = repository.uploadImage(file)
                result.onSuccess { response ->
                    _message.value = "Uploaded! ID: ${response.id}"
                    loadAllImages() // List refresh karo
                }.onFailure { error ->
                    _message.value = "Upload failed: ${error.message}"
                }
            } else {
                _message.value = "File not found!"
            }
            _isLoading.value = false
        }
    }

    // ✅ Delete Image
    fun deleteImage(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteImage(id)
            result.onSuccess {
                _message.value = "Image deleted!"
                loadAllImages() // List refresh karo
            }.onFailure { error ->
                _message.value = "Delete failed: ${error.message}"
            }
            _isLoading.value = false
        }
    }

    // ✅ Message clear karo
    fun clearMessage() {
        _message.value = null
    }

    // ✅ Uri to File converter
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val extension = when (mimeType) {
                "image/png" -> ".png"
                "image/gif" -> ".gif"
                "image/webp" -> ".webp"
                else -> ".jpg"
            }
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}$extension")
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
