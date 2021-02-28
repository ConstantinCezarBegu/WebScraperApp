package com.constantin.webscraperapp.ui.fragments.googleweblist

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.ImageLoader
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.databinding.FragmentSearchGoogleWebOnlineBinding
import com.constantin.webscraperapp.ui.ViewBindingFragment
import com.constantin.webscraperapp.util.IOnBackPressed
import com.constantin.webscraperapp.util.disableAnimations
import com.constantin.webscraperapp.util.shareArticleIntent
import com.constantin.webscraperapp.viewmodel.GoogleWebOnlineViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoogleWebListOnlineFragment : ViewBindingFragment<FragmentSearchGoogleWebOnlineBinding>(
    FragmentSearchGoogleWebOnlineBinding::inflate
) {

    private val viewModel: GoogleWebOnlineViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader
    private lateinit var recyclerViewAdapter: GoogleWebListRecyclerView

    @Inject
    lateinit var customTabsIntent: CustomTabsIntent

    override fun onBindingCreated(
        binding: FragmentSearchGoogleWebOnlineBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachToolbar()
            attachRecyclerView()
        }
    }

    private fun FragmentSearchGoogleWebOnlineBinding.attachToolbar() {
        googleWebSearchToolbar.run {
            googleSearch.setText(viewModel.googleWebSearchKey)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.searchWebMenuItem -> {
                        findNavController().navigate(
                            GoogleWebListOnlineFragmentDirections.actionGoogleWebListOnlineFragmentSelf(
                                googleSearch.text.toString()
                            )
                        )
                    }
                    R.id.imageToggleMenuItem -> {
                        findNavController().navigate(
                            GoogleWebListOnlineFragmentDirections.actionGoogleWebListOnlineFragmentToGoogleImageOnlineListFragment(
                                viewModel.googleWebSearchKey
                            )
                        )
                    }
                }
                true
            }
        }
    }

    private fun FragmentSearchGoogleWebOnlineBinding.attachRecyclerView() {
        googleWebOnlineRecyclerView.disableAnimations()

        recyclerViewAdapter = GoogleWebListRecyclerView(
            itemClickCallback = { url, _, _ ->
                launchInBrowser(url)
            },
            itemShareClickCallback = { title, url, _ ->
                startActivity(shareArticleIntent(title, url))
            },
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            googleWebOnlineRecyclerView.adapter = it
        }

        viewModel.googleImagePagedList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }

        googleWebOnlineRecyclerView.layoutManager = StaggeredGridLayoutManager(
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                Configuration.ORIENTATION_LANDSCAPE -> 2
                else -> 2
            }, StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun launchInBrowser(url: String) {

        customTabsIntent.launchUrl(
            requireContext(),
            Uri.parse(url)
        )
    }
}