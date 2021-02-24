package cn.chitanda.gallery.network

import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.data.Pixaby
import cn.chitanda.gallery.data.VideoHit
import cn.chitanda.gallery.data.Videos
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
    fun getImages(
        @Query("key") key: String ,
        @Query("page") page: Int
    ): Call<Pixaby<ImageHit>>

    @GET("/api/videos")
    fun getVideos(
        @Query("key") key: String ,
        @Query("page") page: Int
    ): Call<Pixaby<VideoHit>>
}