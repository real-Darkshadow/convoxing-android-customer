package com.convoxing.convoxing_customer.ui.call.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.convoxing.convoxing_customer.databinding.ItemCoinPackageBinding

class CoinPackageAdapter(
    private var productDetailsList: List<ProductDetails>,
    private val onItemClick: (ProductDetails) -> Unit
) : RecyclerView.Adapter<CoinPackageAdapter.CoinPackageViewHolder>() {

    fun updateData(newProductDetailsList: List<ProductDetails>) {
        productDetailsList = newProductDetailsList
        notifyDataSetChanged()
    }

    inner class CoinPackageViewHolder(private val binding: ItemCoinPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(productDetails: ProductDetails) {
            // Extract coin amount from product ID or title
            val coinAmount = when (productDetails.productId) {
                "com.convoxing.100coins" -> "100 Coins"
                "com.convoxing.500coins" -> "500 Coins"
                "com.convoxing.1000coins" -> "1000 Coins"
                else -> productDetails.name
            }

            // Set description based on coin amount
            val description = when (productDetails.productId) {
                "com.convoxing.100coins" -> "Good for 5 practice sessions"
                "com.convoxing.500coins" -> "Good for 25 practice sessions"
                "com.convoxing.1000coins" -> "Good for 50 practice sessions"
                else -> ""
            }

            binding.tvCoinAmount.text = coinAmount
            binding.tvCoinDescription.text = description
            binding.tvPrice.text =
                productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: "Price unavailable"

            binding.root.setOnClickListener {
                onItemClick(productDetails)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinPackageViewHolder {
        val binding = ItemCoinPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoinPackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinPackageViewHolder, position: Int) {
        holder.bind(productDetailsList[position])
    }

    override fun getItemCount(): Int = productDetailsList.size
}