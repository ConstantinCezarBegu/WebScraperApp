package com.constantin.webscraperapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.LoadRequest
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.databinding.FragmentGoogleImageBinding
import com.constantin.webscraperapp.ui.ViewBindingFragment
import com.constantin.webscraperapp.util.GoogleImageDisplayState
import com.constantin.webscraperapp.util.changeMenu
import com.constantin.webscraperapp.util.shareArticleIntent
import com.constantin.webscraperapp.viewmodel.GoogleImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.stream.DoubleStream
import javax.inject.Inject

@AndroidEntryPoint
class GoogleImageFragment : ViewBindingFragment<FragmentGoogleImageBinding>(
    FragmentGoogleImageBinding::inflate
) {
    private val viewModel: GoogleImageViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onBindingCreated(
        binding: FragmentGoogleImageBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachToolbar()
            attachImage()
        }
    }

    private fun FragmentGoogleImageBinding.attachToolbar() {
        googleImageToolbar.run {
            this.menu[1].setUpImageActionIcon(context)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.shareMenuItem -> {
                        startActivity(shareArticleIntent("image", viewModel.googleImageUrl))
                    }
                    R.id.saveMenuItem -> {
                        viewModel.imageAction()
                        menuItem.setUpImageActionIcon(context)
                    }
                    R.id.downloadMenuItem -> {
                        activity?.let {
                            viewModel.downloadImage(it) {
                                Toast.makeText(context, getString(R.string.google_image_downloaded), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                true
            }
        }
    }

    private fun FragmentGoogleImageBinding.attachImage() {
        googleImage.run {
            imageLoader.execute(
                LoadRequest.Builder(context)
                    .data(viewModel.googleImageUrl)
                    .listener(
                        onError = { _, _ ->
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_delete
                                )
                            )
                        }
                    )
                    .target(this)
                    .apply { DoubleStream.builder() }
                    .build()
            )
        }
    }

    private fun MenuItem.setUpImageActionIcon(context: Context) {
        if (viewModel.googleImageState == GoogleImageDisplayState.Saved) {
            changeMenu(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_delete
                )!!, R.string.action_delete
            )
        } else {
            changeMenu(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_save
                )!!, R.string.action_save
            )
        }
    }

}