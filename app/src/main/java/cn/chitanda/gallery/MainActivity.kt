package cn.chitanda.gallery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.chitanda.gallery.ui.page.HomePage

import cn.chitanda.gallery.ui.theme.GalleryTheme
import cn.chitanda.gallery.ui.view.rika.glide.ProvideGlideLoader


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalleryTheme {
                val navigation = rememberNavController()
                NavHost(navController = navigation, startDestination = "home") {
                    composable("home") { ProvideGlideLoader { HomePage() } }
                }

            }
        }
    }
}