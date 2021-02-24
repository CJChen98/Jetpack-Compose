package cn.chitanda.gallery.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.data.VideoHit
import cn.chitanda.gallery.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author:       Chen
 * @Date:         2021/2/20 16:08
 * @Description:
 */
class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    var images by mutableStateOf<List<ImageHit>>(listOf())
        private set
    var videos by mutableStateOf<List<VideoHit>>(listOf())
        private set

    fun fetchImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = NetworkManager.getImages().hits
            images = listOf(images, list).flatten()
        }
    }

    fun fetchVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = NetworkManager.getVideos().hits
            videos = listOf(videos, list).flatten()
        }
    }
}