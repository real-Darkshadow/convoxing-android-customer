package com.convoxing.convoxing_customer.ui.practice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.Scenario
import com.convoxing.convoxing_customer.databinding.ItemPracticeFragRvBinding
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isNotNullOrBlank


class PracticeFragAdapter(
    private val data: ArrayList<Scenario>, // Data list accessible in the adapter
    private val onItemClick: (Scenario) -> Unit // Lambda to handle item click
) : RecyclerView.Adapter<PracticeFragAdapter.ViewHolder>() {

    // ViewHolder class to bind the theme item and handle clicks
    class ViewHolder(private val binding: ItemPracticeFragRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Scenario, onItemClick: (Scenario) -> Unit) {
            binding.tvTitle.text = data.loadingScreenInfo.title
            if (data.imageUrl.isNotNullOrBlank()) {
                val glideUrl = GlideUrl(
                    data.imageUrl,
                    LazyHeaders.Builder()
                        .addHeader(
                            "X-Appwrite-Key",
                            BuildConfig.APPWRITE_KEY
                        )
                        .build()
                )
                Glide.with(binding.root.context)
                    .load(glideUrl)
                    .error(R.drawable.yeah)
                    .transform(CenterCrop())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.image)
            } else binding.image.setImageResource(R.drawable.yeah)
            // Set the onClickListener on the root of the item layout
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Pass the clicked theme to the onItemClick callback
                    onItemClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPracticeFragRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = data[position]
        holder.bind(theme, onItemClick) // Pass the theme and the click handler to the ViewHolder
    }

    override fun getItemCount(): Int = data.size

    // Function to update the list of items in the adapter
    fun updateItem(items: ArrayList<Scenario>) {
        val currentSize: Int = data.size
        data.clear()
        data.addAll(items)
        notifyItemRangeRemoved(0, currentSize)
        notifyItemRangeInserted(0, items.size)
    }
}