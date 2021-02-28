package com.constantin.webscraperapp.ui.fragments.googleimagelist

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil.ImageLoader
import coil.request.LoadRequest
import com.constantin.webscraperapp.R
import com.constantin.webscraperapp.databinding.ListItemGoogleImageBinding
import com.constantin.webscraperapp.util.RecyclerViewAdapter
import java.util.stream.DoubleStream.builder

class GoogleImageListRecyclerView(
    @DrawableRes private val saveStateIcon: Int,
    private val imageLoader: ImageLoader,
    itemClickCallback: (url: String, context: Context, Int) -> Unit = { _, _, _ -> },
    itemLongClickCallback: (url: String, context: Context, Int) -> Unit = { _, _, _ -> },
    private val itemShareClickCallback: (url: String, view: View) -> Unit,
    private val itemSaveClickCallback: (url: String, view: View) -> Unit
) : RecyclerViewAdapter<String, ListItemGoogleImageBinding>(
    viewInflater = ListItemGoogleImageBinding::inflate,
    itemClickCallback = itemClickCallback,
    itemLongClickCallback = itemLongClickCallback,
    diffItemCallback = diffItemCallback
) {
    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem
        }
    }

    override val String.id: String
        get() = this

    override fun onBindingCreated(item: String, binding: ListItemGoogleImageBinding) {
        binding.run {
            googleImagePreview.run {
                imageLoader.execute(
                    LoadRequest.Builder(context)
                        .data(item)
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
                        .apply { builder() }
                        .build()
                )
            }

            googleImageShare.setOnClickListener { view ->
                itemShareClickCallback(item, view)
            }

            googleImageSave.setImageDrawable(
                ContextCompat.getDrawable(
                    googleImageSave.context,
                    saveStateIcon
                )
            )
            googleImageSave.setOnClickListener { view ->
                itemSaveClickCallback(item, view)
            }
        }
    }
}