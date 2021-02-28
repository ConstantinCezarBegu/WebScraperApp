package com.constantin.webscraperapp.ui

import androidx.appcompat.app.AppCompatActivity
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.network.GoogleImageService
import com.constantin.webscraperapp.util.IOnBackPressed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    @Inject
    lateinit var googleImageService: GoogleImageService

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager
                .primaryNavigationFragment
                ?.childFragmentManager
                ?.fragments
                ?.firstOrNull { it.isVisible }
        if (fragment is IOnBackPressed) {
            if (fragment.onBackPressed()) super.onBackPressed()
        } else super.onBackPressed()
    }
}