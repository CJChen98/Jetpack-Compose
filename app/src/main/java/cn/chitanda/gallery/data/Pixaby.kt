package cn.chitanda.gallery.data


import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Pixaby<T : Hit>(
    @SerializedName("hits")
    val hits: List<T>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalHits")
    val totalHits: Int
)