package com.constantin.webscraperapp.ui.fragments.googleimagelist

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.ImageLoader
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.databinding.FragmentSearchGoogleImageOnlineBinding
import com.constantin.webscraperapp.ui.ViewBindingFragment
import com.constantin.webscraperapp.util.IOnBackPressed
import com.constantin.webscraperapp.util.disableAnimations
import com.constantin.webscraperapp.util.shareArticleIntent
import com.constantin.webscraperapp.util.GoogleImageDisplayState
import com.constantin.webscraperapp.viewmodel.GoogleImagesOnlineViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoogleImageListOnlineFragment : ViewBindingFragment<FragmentSearchGoogleImageOnlineBinding>(
    FragmentSearchGoogleImageOnlineBinding::inflate
), IOnBackPressed {
    private val viewModel: GoogleImagesOnlineViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader
    private lateinit var recyclerViewAdapter: GoogleImageListRecyclerView

    override fun onBackPressed(): Boolean {
        findNavController().navigate(
            GoogleImageListOnlineFragmentDirections.actionGoogleImageOnlineListFragmentToGoogleWebListOnlineFragment(
                viewModel.googleImageSearchKey
            )
        )
        return false
    }

    override fun onBindingCreated(
        binding: FragmentSearchGoogleImageOnlineBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachToolbar()
            attachRecyclerView()
        }
    }

    private fun FragmentSearchGoogleImageOnlineBinding.attachToolbar() {
        googleImageSearchToolbar.run {
            googleSearch.setText(viewModel.googleImageSearchKey)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.searchImageMenuItem -> {
                        findNavController().navigate(
                            GoogleImageListOnlineFragmentDirections.actionGoogleImageOnlineListFragmentSelf(
                                googleSearch.text.toString()
                            )
                        )
                    }
                    R.id.imageStateMenuItem -> {
                        findNavController().navigate(
                            GoogleImageListOnlineFragmentDirections.actionGoogleImageListFragmentToGoogleImageListSavedFragment()
                        )
                    }
                    R.id.webToggleMenuItem -> {
                        findNavController().navigate(
                            GoogleImageListOnlineFragmentDirections.actionGoogleImageOnlineListFragmentToGoogleWebListOnlineFragment(
                                googleSearch.text.toString()
                            )
                        )
                    }
                }
                true
            }
        }
    }

    private fun FragmentSearchGoogleImageOnlineBinding.attachRecyclerView() {
        googleImageOnlineRecyclerView.disableAnimations()

        recyclerViewAdapter = GoogleImageListRecyclerView(
            imageLoader = imageLoader,
            itemClickCallback = { imageUrl, _, _ ->
                findNavController().navigate(
                    GoogleImageListOnlineFragmentDirections.actionGoogleImageListFragmentToGoogleImageFragment(
                        imageUrl,
                        GoogleImageDisplayState.Online
                    )
                )
            },
            itemShareClickCallback = { url, _ ->
                startActivity(shareArticleIntent(viewModel.googleImageSearchKey, url))
            },
            itemSaveClickCallback = { url, view ->
                viewModel.saveImage(url)
                (view as AppCompatImageButton).setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        R.drawable.ic_check
                    )
                )
            },
            saveStateIcon = R.drawable.ic_save
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            googleImageOnlineRecyclerView.adapter = it
        }

        viewModel.googleImagePagedList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }

        googleImageOnlineRecyclerView.layoutManager = StaggeredGridLayoutManager(
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                Configuration.ORIENTATION_LANDSCAPE -> 2
                else -> 2
            }, StaggeredGridLayoutManager.VERTICAL
        )
    }
}