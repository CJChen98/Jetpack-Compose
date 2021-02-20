package cn.chitanda.gallery.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.chitanda.gallery.R
import cn.chitanda.gallery.data.ImageHit
import cn.chitanda.gallery.ui.view.NetworkImage
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
                Box(Modifier.padding(start = 16.dp,top = 8.dp)) {
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
            Box(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                NetworkImage(
                    url = item.webformatURL,
                    contentDescription = "image",
                    modifier = Modifier.fillMaxSize().clip(
                        RoundedCornerShape(3.dp)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageListItemPreview() {
    val item =
        ImageHit(
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            "",
            0,
            "",
            0,
            "",
            0,
            "",
            "",
            "chen",
            0,
            "",
            0,
            0,
            "https://pixabay.com/get/35bbf209e13e39d2_640.jpg",
            0
        )
    ImageListItem(item)
}