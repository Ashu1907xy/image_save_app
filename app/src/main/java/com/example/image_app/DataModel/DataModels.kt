package com.example.image_app.DataModel

data class UploadResponse(
    val message: String,
    val id: String
)

data class ImageItem(
    val id: String,
    val filename: String,
    val contentType: String
)

data class DeleteResponse(
    val message: String,
    val id: String
)