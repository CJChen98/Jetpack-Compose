package cn.chitanda.gallery.ui.view


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import cn.chitanda.gallery.R
import cn.chitanda.gallery.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 *@author: Chen
 *@createTime: 2021/2/20 22:11
 *@description:
 **/

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth
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
        Image(
            painter = painterResource(id = R.drawable.ic_loading_image),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )

        LaunchedEffect(url) {
            bitmap =
                withContext(Dispatchers.IO) { NetworkManager.downloadBitmap(url) }?.asImageBitmap()
        }
    }
}