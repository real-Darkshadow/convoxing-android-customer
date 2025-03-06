package com.convoxing.convoxing_customer.ui.auth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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

                // Handle icon if present
//                if (item.iconResId != null) {
//                    icon.setImageResource(item.iconResId!!)
//                    icon.visibility = View.VISIBLE
//                } else {
//                    icon.visibility = View.GONE
//                }

                if (selectedPosition == position) {
                    container.setBackgroundResource(R.drawable.primary_button_bg)
                    container.backgroundTintList = null
                    title.setTextColor(ContextCompat.getColor(context, R.color.primary_text_white))
                    // Animate selection with slight bounce
                    container.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).withEndAction {
                        container.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }.start()
                } else {
                    container.setBackgroundResource(R.drawable.primary_button_bg)
                    container.backgroundTintList = 
                        ContextCompat.getColorStateList(context, R.color.anti_flash_white)
                    title.setTextColor(ContextCompat.getColor(context, R.color.primary_text_dark))
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