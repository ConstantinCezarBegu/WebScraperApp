package com.constantin.webscraperapp.ui.fragments.googleweblist

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.constantin.webscraperapp.databinding.ListItemGoogleWebBinding
import com.constantin.webscraperapp.network.WebSearch
import com.constantin.webscraperapp.util.RecyclerViewAdapter

class GoogleWebListRecyclerView(
    itemClickCallback: (url: String, context: Context, Int) -> Unit = { _, _, _ -> },
    itemLongClickCallback: (url: String, context: Context, Int) -> Unit = { _, _, _ -> },
    private val itemShareClickCallback: (title: String, url: String, view: View) -> Unit,
) : RecyclerViewAdapter<WebSearch, ListItemGoogleWebBinding>(
    viewInflater = ListItemGoogleWebBinding::inflate,
    itemClickCallback = itemClickCallback,
    itemLongClickCallback = itemLongClickCallback,
    diffItemCallback = diffItemCallback
) {
    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<WebSearch>() {
            override fun areItemsTheSame(
                oldItem: WebSearch,
                newItem: WebSearch
            ): Boolean = oldItem === newItem

            override fun areContentsTheSame(
                oldItem: WebSearch,
                newItem: WebSearch
            ): Boolean = oldItem == newItem
        }
    }

    override val WebSearch.id: String
        get() = this.url

    override fun onBindingCreated(item: WebSearch, binding: ListItemGoogleWebBinding) {
        binding.run {

            googleWebTitle.text = item.title

            googleWebDescription.text = item.description

            googleWebShare.setOnClickListener { view ->
                itemShareClickCallback(item.title, item.url, view)
            }
        }
    }
}