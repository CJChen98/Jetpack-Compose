package cn.chitanda.gallery.ui.view


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.async
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.roundToInt


/**
 *@author: Chen
 *@createTime: 2021/2/20 22:11
 *@description:
 **/
private const val TAG = "NetworkImage"

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    width: Int = 0,
    height: Int = 0
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Center {
            CircularProgressIndicator()
        }
        LaunchedEffect(url) {
            bitmap =
                async {
                    RequestManager.downloadBitmap(
                        url,
                        width,
                        height
                    )
                }.await()?.asImageBitmap()
        }
    }
}

object RequestManager {
    private val okHttpClient by lazy { OkHttpClient() }

    suspend fun downloadBitmap(url: String, width: Int, height: Int): Bitmap? {
        if (!(url.startsWith("https://")||url.startsWith("http://"))){
            Log.e(TAG, "downloadBitmap error: $url")
            return null
        }
        val request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request = request)
        return call.await { _, response ->
            val body = response.body
            var bitmap: Bitmap? = null
            if (body != null) {
                bitmap = try {
                    val bytes = body.byteStream().readBytes()
                    val options =
                        BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    options.inSampleSize =
                        calculateInSampleSize(options, width, height)
                    options.inJustDecodeBounds = false
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options).also {
                        Log.d(TAG, "downloadBitmap: $it")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
            bitmap
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        requestWidth: Int,
        requestHeight: Int
    ): Int {
        if (requestWidth == 0 || requestHeight == 0) return 1
        val height = options.outHeight
        val width = options.outWidth
        return if (height > requestHeight || width > requestWidth) {
            val widthScale = (width.toFloat() / requestWidth.toFloat()).roundToInt()
            val heightScale = (height.toFloat() / requestHeight.toFloat()).roundToInt()
            max(widthScale, heightScale)
        } else {
            1
        }
    }
}

suspend inline fun <T> Call.await(crossinline onResponse: (call: Call, response: Response) -> T?): T? {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resume(null)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resume(onResponse(call, response))
            }
        })
    }
}

