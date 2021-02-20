package cn.chitanda.gallery.data


import com.google.gson.annotations.SerializedName

data class Medium(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)