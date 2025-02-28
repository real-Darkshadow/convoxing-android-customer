package com.convoxing.convoxing_customer.ui.practice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.SessionHistory
import com.convoxing.convoxing_customer.data.remote.models.Scenario
import com.convoxing.convoxing_customer.databinding.ItemPracticeFragRvBinding
import com.convoxing.convoxing_customer.databinding.ItemSessionHistoryRvBinding
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isNotNullOrBlank
import com.convoxing.convoxing_customer.utils.Utils.formatIsoDate


class SessionHistoryAdapter(
    private val data: ArrayList<SessionHistory>, // Data list accessible in the adapter
    private val onItemClick: (SessionHistory) -> Unit // Lambda to handle item click
) : RecyclerView.Adapter<SessionHistoryAdapter.ViewHolder>() {

    // ViewHolder class to bind the theme item and handle clicks
    class ViewHolder(private val binding: ItemSessionHistoryRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SessionHistory, onItemClick: (SessionHistory) -> Unit) {
            binding.name.text = "${data.category} - ${data.sessionName}"
            binding.date.text = data.createdAt.formatIsoDate()
            // Set the onClickListener on the root of the item layout
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSessionHistoryRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = data[position]
        holder.bind(theme, onItemClick) // Pass the theme and the click handler to the ViewHolder
    }

    override fun getItemCount(): Int = data.size

    // Function to update the list of items in the adapter
    fun updateItem(items: ArrayList<SessionHistory>) {
        val currentSize: Int = data.size
        data.clear()
        data.addAll(items)
        notifyItemRangeRemoved(0, currentSize)
        notifyItemRangeInserted(0, items.size)
    }
}