package nr.dev.ezemkofi

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun NetImage(url: String, modifier: Modifier = Modifier, description: String = "?", contentScale: ContentScale = ContentScale.Fit) {
    val imgLoader = remember { ImageLoader() }
    var img by remember { mutableStateOf<ImageBitmap?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(url) {
        if(url.isNotBlank()) {
            if(!imgLoader.hasCache(url)) loading = true
            error = false
            try {
                img = imgLoader.getImage(url)
                error = img == null
            } catch (e: Exception) {
                e.printStackTrace()
                error = true
            } finally {
                loading = false
            }
        }
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        when {
            loading -> {
                val infTrans = rememberInfiniteTransition()
                val col by infTrans.animateColor(Color.White, Color.LightGray, animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing), repeatMode = RepeatMode.Reverse))
                Box(Modifier.fillMaxSize().background(col))
            }
            error -> {
                TextP("Failed to load Image")
            }
            img != null -> {
                Image(img!!, contentDescription = description, modifier = Modifier.fillMaxSize(), contentScale = contentScale)
            }
        }
    }
}

class ImageLoader {
    val caches = mutableMapOf<String, ImageBitmap>()

    suspend fun getImage(url: String): ImageBitmap? {
        caches[url]?.let { return it }
        val img = HttpClient.fetchImg(url)
        if(img != null) caches[url] = img
        return img
    }

    fun hasCache(url: String): Boolean = caches.contains(url)
}