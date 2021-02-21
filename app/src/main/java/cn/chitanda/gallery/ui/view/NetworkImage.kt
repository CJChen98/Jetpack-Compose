package cn.chitanda.gallery.ui.view


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import cn.chitanda.gallery.R
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
//        val context = LocalContext.current
        Image(
            painter = painterResource(id = R.drawable.ic_loading_image),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )

        LaunchedEffect(url) {
            bitmap =
                async {
                    NetworkManager.downloadBitmap(
                        url,
                        width,
                        height
                    )
                }.await()?.asImageBitmap()
        }
    }
}

object NetworkManager {
    private val okHttpClient by lazy { OkHttpClient() }

    suspend fun downloadBitmap(url: String, width: Int, height: Int): Bitmap? {
        val request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request = request)
        return suspendCoroutine { continuation ->
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resume(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body
                    if (body != null) {
                        val bitmap = try {
                            val bytes = body.byteStream().readBytes()
                            val options =
                                BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                            options.inSampleSize = calculateInSampleSize(options, width, height)
                            options.inJustDecodeBounds = false
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options).also {
                                Log.d(TAG, "downloadBitmap: $it")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            null
                        }
                        continuation.resume(bitmap)
                    }
                }
            })
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