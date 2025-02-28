package com.convoxing.convoxing_customer.ui.progress.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.ProfileOptionsBinding

class SettingsAdapter(
    private val settingsOptions: Map<Int, Pair<String, String>>,
    val onclick: (Int) -> Unit,
) : RecyclerView.Adapter<SettingsAdapter.VH>() {

    inner class VH(val binding: ProfileOptionsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            ProfileOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = settingsOptions.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val settingOption = settingsOptions[position]
        holder.binding.apply {
            orderName.text = settingOption?.first

            // Set icon based on setting type
//            val iconResId = when (settingOption?.second) {
//                "profile" -> R.drawable.ic_person
//                "notifications" -> R.drawable.ic_notifications
//                "logout" -> R.drawable.ic_logout
//                "help" -> R.drawable.ic_help
//                "contact" -> R.drawable.ic_email
//                "share" -> R.drawable.ic_share
//                "privacy" -> R.drawable.ic_lock
//                "terms" -> R.drawable.ic_document
//                "delete" -> R.drawable.ic_delete
//                else -> R.drawable.ic_settings
//            }
//            optionIcon.setImageResource(iconResId)

            // Set special color for logout and delete account
            if (settingOption?.second == "logout" || settingOption?.second == "delete") {
                orderName.setTextColor(root.context.getColor(R.color.nc_darkRed))
                optionIcon.setColorFilter(root.context.getColor(R.color.nc_darkRed))
            }

            root.setOnClickListener { onclick(position) }
        }
    }
}