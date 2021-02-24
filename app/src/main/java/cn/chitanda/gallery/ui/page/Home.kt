package cn.chitanda.gallery.ui.page

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.chitanda.gallery.R
import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.data.VideoHit
import cn.chitanda.gallery.databinding.VideoItemBinding
import cn.chitanda.gallery.ui.view.Center
import cn.chitanda.gallery.ui.view.NetworkImage
import cn.chitanda.gallery.viewmodel.GalleryViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * @Author:       Chen
 * @Date:         2021/2/20 14:25
 * @Description:
 */
@Composable
fun HomePage() {
    Scaffold(
        topBar = {
            TopAppBar(Modifier.background(MaterialTheme.colors.primary)) {
                Box(Modifier.padding(start = 16.dp, top = 8.dp)) {
                    Text(
                        text = "Pixaby",
                        style = TextStyle(
                            color = MaterialTheme.colors.surface,
                            fontSize = 28.sp
                        )
                    )
                }
            }
        }
    ) {
        val viewModel: GalleryViewModel = viewModel()
        Column(modifier = Modifier.fillMaxSize()) {
            var currentPage by remember { mutableStateOf(0) }
            TabRow(selectedTabIndex = currentPage) {
                Text(
                    text = "Picture", textAlign = TextAlign.Center,
                    fontStyle = MaterialTheme.typography.caption.fontStyle,
                    modifier = Modifier.clickable { currentPage = 0 }.padding(16.dp)
                )
                Text(
                    text = "Video", textAlign = TextAlign.Center,
                    fontStyle = MaterialTheme.typography.caption.fontStyle,
                    modifier = Modifier.clickable { currentPage = 1 }.padding(16.dp)
                )
            }

            when (currentPage) {
                0 -> {
                    ImageList(viewModel)
                }
                1 -> {
                    VideoList(viewModel)
                }
            }
        }
    }
}

@Composable
private fun ImageList(viewModel: GalleryViewModel) {
    Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        if (viewModel.images.isEmpty()) {
            Center(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.background(MaterialTheme.colors.background).fillMaxWidth().fillMaxSize()
            ) {
                itemsIndexed(viewModel.images) { _, item ->
                    ImageListItem(item)
                }
            }
        }
    }
    if (viewModel.images.isEmpty()) {
        viewModel.fetchImages()
    }
}

@Composable
private fun ImageListItem(item: ImageHit) {
    Card(elevation = 2.dp, modifier = Modifier.padding(16.dp)) {
        Column(Modifier.clickable { }.fillMaxWidth()) {
            Row(Modifier.padding(8.dp)) {
                NetworkImage(
                    url = item.userImageURL,
                    contentDescription = "avatar",
                    modifier = Modifier.padding(8.dp).size(48.dp).clip(CircleShape).clickable { }
                )
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(text = item.user)
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.ic_favorite),
                            contentDescription = "likes",
                            modifier = Modifier.padding(end = 8.dp).size(20.dp)
                        )
                        Text(text = item.likes.toString())
                    }
                }
            }
            NetworkImage(
                url = item.webformatURL,
                contentDescription = "image",
                modifier = Modifier.padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 12.dp)
                    .fillMaxSize().clip(
                        RoundedCornerShape(3.dp)
                    ), width = item.webformatWidth, height = item.webformatHeight
            )
        }
    }
}

@Composable
fun VideoList(viewModel: GalleryViewModel) {
    Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        if (viewModel.videos.isEmpty()) {
            Center(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.background(MaterialTheme.colors.background).fillMaxWidth().fillMaxSize()
            ) {
                itemsIndexed(viewModel.videos) { _, item ->
                    VideoListItem(item)
                }
            }
        }
    }
    if (viewModel.videos.isEmpty()) {
        viewModel.fetchVideos()
    }
}

@Composable
fun VideoListItem(item: VideoHit) {
    val uri = item.videos.medium.url
    Card(elevation = 2.dp, modifier = Modifier.padding(16.dp)) {
        Column(Modifier.clickable { }.fillMaxWidth()) {
            Row(Modifier.padding(8.dp)) {
                NetworkImage(
                    url = item.userImageURL,
                    contentDescription = "avatar",
                    modifier = Modifier.padding(8.dp).size(48.dp).clip(CircleShape).clickable { }
                )
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(text = item.user)
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.ic_favorite),
                            contentDescription = "likes",
                            modifier = Modifier.padding(end = 8.dp).size(20.dp)
                        )
                        Text(text = item.likes.toString())
                    }
                }
            }

            // This is the official way to access current context from Composable functions
            val context = LocalContext.current

            // Do not recreate the player everytime this Composable commits
            val exoPlayer = remember {
                SimpleExoPlayer.Builder(context).build()
            }
            var source by remember { mutableStateOf<ProgressiveMediaSource?>(null) }
            // Gateway to legacy Android Views through XML inflation.
            /*    AndroidView(modifier = Modifier.fillMaxSize(), viewBlock = { context ->
                    PlayerView(context).apply {
                        useController = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setOnClickListener {
                            source?.let { s -> exoPlayer.prepare(s) }
                        }
                    }
                }, update = { view ->
                    view.apply {
                        player = exoPlayer
                        exoPlayer.playWhenReady =true
                    }

                })*/
            AndroidViewBinding(VideoItemBinding::inflate, modifier = Modifier.fillMaxSize()) {
                playerView.player = exoPlayer
                exoPlayer.playWhenReady = true
                source?.let { it1 -> exoPlayer.prepare(it1) }
                playerView.setOnClickListener {
                }
            }
            LaunchedEffect(uri) {
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.packageName)
                )

                source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        Uri.parse(
                            // Big Buck Bunny from Blender Project
                            uri
                        )
                    )
            }
        }
    }
}