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

    suspend fun downloadBitmap(url: String): Bitmap? {
        val request = Request.Builder().url(url).build()
        val call = okHttpClient.newCall(request = request)
        val body = call.execute().body
        return if (body != null) {
            try {
                val byteStream = body.byteStream()
                BitmapFactory.decodeStream(byteStream).also {
                    Log.d(TAG, "downloadBitmap: $it")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
//        return@withContext null
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