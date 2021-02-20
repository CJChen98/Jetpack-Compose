package cn.chitanda.gallery.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.chitanda.gallery.data.ImageHit
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

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = NetworkManager.getImages().hits
            images = listOf(images, list).flatten()
            Log.d("ViewModel", "fetchData: ${list.size}")
        }
    }
}