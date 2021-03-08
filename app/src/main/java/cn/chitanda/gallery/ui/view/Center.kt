package cn.chitanda.gallery.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * @Author:       Chen
 * @Date:         2021/2/24 15:50
 * @Description:
 */
@Composable
fun Center(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}