package com.convoxing.convoxing_customer.ui.auth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.models.OnboardingScreenData
import com.convoxing.convoxing_customer.databinding.ItemOnboardingRvCardBinding

class OnboardingDetailsAdapter(
    private var data: ArrayList<OnboardingScreenData>,
    val onClick: (String) -> Unit,
) : RecyclerView.Adapter<OnboardingDetailsAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var selectedPosition: Int = -1

    inner class ViewHolder(private val binding: ItemOnboardingRvCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingScreenData, position: Int) {
            binding.apply {
                title.text = item.title

                if (selectedPosition == position) {
                    container.setBackgroundResource(R.drawable.primary_button_bg)
                    container.backgroundTintList = null
                } else {
                    container.setBackgroundResource(R.drawable.primary_button_bg)
                    container.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.anti_flash_white)
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onClick(item.title)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        return ViewHolder(ItemOnboardingRvCardBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: ArrayList<OnboardingScreenData>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun clearData() {
        selectedPosition = -1
        data.clear()
        notifyDataSetChanged()
    }
}