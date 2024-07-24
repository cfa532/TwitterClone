import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AttachmentIcon(uri: Uri) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val canvasSize = screenWidth / 2
    val contentResolver = LocalContext.current.contentResolver
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = contentResolver.loadThumbnail(
                    uri, android.util.Size(canvasSize, canvasSize), null
                )
                val resizedBitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(resizedBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                val (scaledWidth, scaledHeight) = if (aspectRatio > 1) {
                    // Landscape image
                    canvasSize to (canvasSize / aspectRatio).toInt()
                } else {
                    // Portrait or square image
                    (canvasSize * aspectRatio).toInt() to canvasSize
                }

                val xOffset = (canvasSize - scaledWidth) / 2
                val yOffset = (canvasSize - scaledHeight) / 2
                canvas.drawBitmap(
                        bitmap,
                null,
                Rect(xOffset, yOffset, xOffset + scaledWidth, yOffset + scaledHeight),
                paint
                )

                imageBitmap = resizedBitmap.asImageBitmap()
            } catch (e: Exception) {
                println("Error loading thumbnail: ${e.message}")
            }
        }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Attached File",
            modifier = Modifier.size(canvasSize.dp)
        )
    } ?: run {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Attached File",
            modifier = Modifier.size(canvasSize.dp)
        )
    }
}