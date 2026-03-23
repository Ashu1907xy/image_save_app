package com.example.image_app.Screens


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.image_app.Api.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageScreen(
    imageId: String,
    filename: String,
    onBack: () -> Unit,
    onDelete: (String) -> Unit,
    navController: NavHostController,

    ) {

    val imageUrl = "${RetrofitInstance.BASE_URL}api/images/$imageId"
BackHandler(onBack = onBack)
    // ✅ Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ✅ Zoom & Pan state
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // ✅ Image loading state
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Image Delete Karo?") },
            text = { Text("\"$filename\" permanently delete ?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(imageId)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(" Delete Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // ✅ Full Screen Image with Pinch Zoom + Pan
        AsyncImage(
            model = imageUrl,
            contentDescription = filename,
            contentScale = ContentScale.Fit,
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        if (scale > 1f) {
                            offsetX += pan.x
                            offsetY += pan.y
                        } else {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )

        // ✅ Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        // ✅ Top Bar — Back Button + Filename
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .statusBarsPadding()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Filename
            Text(
                text = filename,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        // ✅ Bottom Bar — Delete Button + Image ID
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image ID
            Column {
                Text(
                    text = "Image ID",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = imageId.take(20) + "...",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Delete Button
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete", color = Color.White)
            }
        }

        // ✅ Zoom hint (sirf 1x pe dikhao)
        if (scale == 1f) {
            Text(
                text = "Pinch to zoom",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 120.dp)
            )
        }
    }
}
