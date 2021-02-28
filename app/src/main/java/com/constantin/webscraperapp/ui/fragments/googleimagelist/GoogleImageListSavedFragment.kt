package com.constantin.webscraperapp.ui.fragments.googleimagelist

import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.ImageLoader
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.databinding.FragmentSearchGoogleImageSavedBinding
import com.constantin.webscraperapp.ui.ViewBindingFragment
import com.constantin.webscraperapp.util.disableAnimations
import com.constantin.webscraperapp.util.shareArticleIntent
import com.constantin.webscraperapp.util.GoogleImageDisplayState
import com.constantin.webscraperapp.viewmodel.GoogleImagesSavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoogleImageListSavedFragment : ViewBindingFragment<FragmentSearchGoogleImageSavedBinding>(
    FragmentSearchGoogleImageSavedBinding::inflate
) {
    private val viewModel: GoogleImagesSavedViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader
    private lateinit var recyclerViewAdapter: GoogleImageListRecyclerView


    override fun onBindingCreated(
        binding: FragmentSearchGoogleImageSavedBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachToolbar()
            attachRecyclerView()
        }
    }

    private fun FragmentSearchGoogleImageSavedBinding.attachToolbar() {
        googleImageSavedToolbar.run {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.imageStateMenuItem -> {
                        findNavController().popBackStack()
                    }
                }
                true
            }
        }
    }

    private fun FragmentSearchGoogleImageSavedBinding.attachRecyclerView() {
        googleImageSavedRecyclerView.disableAnimations()

        recyclerViewAdapter = GoogleImageListRecyclerView(
            imageLoader = imageLoader,
            itemClickCallback = { imageUrl, _, _ ->
                findNavController().navigate(
                    GoogleImageListSavedFragmentDirections.actionGoogleImageListSavedFragmentToGoogleImageFragment(
                        imageUrl,
                        GoogleImageDisplayState.Saved
                    )
                )
            },
            itemShareClickCallback = { url, _ ->
                startActivity(shareArticleIntent("SavedImages", url))
            },
            itemSaveClickCallback = { url, _ ->
                viewModel.deleteImage(url)
            },
            saveStateIcon = R.drawable.ic_delete
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            googleImageSavedRecyclerView.adapter = it
        }

        viewModel.googleImagePagedList.observe(viewLifecycleOwner) {
            noGoogleImageSavedTextView.isGone = it.isNotEmpty()
            recyclerViewAdapter.submitList(it)
        }

        googleImageSavedRecyclerView.layoutManager = StaggeredGridLayoutManager(
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                Configuration.ORIENTATION_LANDSCAPE -> 2
                else -> 2
            }, StaggeredGridLayoutManager.VERTICAL
        )
    }
}