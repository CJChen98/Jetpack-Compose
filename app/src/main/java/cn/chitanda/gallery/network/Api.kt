package cn.chitanda.gallery.network

import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.data.Pixaby
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author:       Chen
 * @Date:         2021/2/20 15:36
 * @Description:
 */
interface Api {
    @GET("/api/")
    fun getAllImage(
        @Query("key") key: String ,
        @Query("page") page: Int
    ): Call<Pixaby<ImageHit>>
}