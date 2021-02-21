package cn.chitanda.gallery.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @Author:       Chen
 * @Date:         2021/2/20 15:26
 * @Description:
 */
private const val TAG = "NetworkManager"

object NetworkManager {
    private val api = RetrofitCreator.create(Api::class.java)
    private val okHttpClient by lazy { OkHttpClient() }
    suspend fun getImages(page: Int = 1) = api.getAllImage(key = getKey(), page = page).await()

    suspend fun downloadBitmap(url: String, width: Int, height: Int): Bitmap? {
        val request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request = request)
        return suspendCoroutine { continuation ->
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    continuation.resume(null)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
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

    private fun getKey(): String = "14598379-6f1338e6d1b1cbb8269b3abae"

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e(TAG, "onFailure: $")
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        Log.d(TAG, "${call.request().url} http response body is null")
                    }
                }
            })
        }
    }
}