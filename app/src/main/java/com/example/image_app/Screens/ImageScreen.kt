package com.example.image_app.Screens


import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.image_app.Api.RetrofitInstance
import com.example.image_app.DataModel.ImageItem
import com.example.image_app.viewmodel.ImageViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(viewModel: ImageViewModel = viewModel(), navController: NavHostController) {

    val context = LocalContext.current
    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    BackHandler(onBack ={navController.navigateUp()})

    // ✅ Full screen navigation state
    var selectedImage by remember { mutableStateOf<ImageItem?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImage(context, it) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllImages()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // ✅ Full Screen Image Screen
    if (selectedImage != null) {
        FullScreenImageScreen(
            imageId = selectedImage!!.id,
            filename = selectedImage!!.filename,
            onBack = { selectedImage = null },
            onDelete = { id ->
                viewModel.deleteImage(id)
                selectedImage = null
            },
            navController = navController
        )
        return
    }

    // ✅ Main List Screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Upload App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        BackHandler {
            navController.navigateUp()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Image Upload Karo")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.loadAllImages() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Refresh List")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Total Images: ${images.size}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (images.isEmpty() && !isLoading) {
                Text(
                    text = "Empty ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(images) { image ->
                        ImageCard(
                            image = image,
                            onDelete = { viewModel.deleteImage(image.id) },
                            onClick = { selectedImage = image }  // ✅ Full screen open
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCard(
    image: ImageItem,
    onDelete: () -> Unit,
    onClick: () -> Unit   // ✅ New click parameter
) {
    val imageUrl = "${RetrofitInstance.BASE_URL}api/images/${image.id}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },  // ✅ Card click = full screen
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = image.filename,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                error = painterResource(android.R.drawable.ic_menu_report_image)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = image.filename,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = image.contentType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tap to view full screen",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}