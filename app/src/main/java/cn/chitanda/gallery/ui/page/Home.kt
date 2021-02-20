package cn.chitanda.gallery.ui.page

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.chitanda.gallery.R
import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.ui.theme.GalleryTheme
import cn.chitanda.gallery.viewmodel.GalleryViewModel

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
                Text(text = "Home")
            }
        }
    ) {
        val viewModel: GalleryViewModel = viewModel()
        Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
            ImageList(viewModel)
        }
        viewModel.fetchData()
    }
}

@Composable
private fun ImageList(viewModel: GalleryViewModel) {
    LazyColumn(Modifier.background(MaterialTheme.colors.background).fillMaxWidth().fillMaxSize()) {
        itemsIndexed(viewModel.images) { _, item ->
            ImageListItem(item)
        }
    }
}

@Composable
private fun ImageListItem(item: ImageHit) {
    Card(elevation = 2.dp, modifier = Modifier.padding(10.dp)) {
        Column(Modifier.clickable { }.fillMaxWidth()) {
            Row(Modifier.padding(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_1),
                    contentDescription = "avatar",
                    modifier = Modifier.padding(10.dp).size(48.dp).clip(CircleShape).clickable { }
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = item.user)
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_favorite),
                            contentDescription = "likes",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = item.likes.toString())
                    }
                }
            }
            //todo     Image(painter = , contentDescription = "image")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageListItemPreview() {
    val item =
        ImageHit(0, 0, 0, 0, 0, 0, 0, "", 0, "", 0, "", 0, "", "", "chen", 0, "", 0, 0, "", 0)
    ImageListItem(item)
}