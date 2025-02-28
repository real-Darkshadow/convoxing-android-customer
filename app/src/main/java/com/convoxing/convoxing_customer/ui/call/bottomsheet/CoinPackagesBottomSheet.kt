package com.convoxing.convoxing_customer.ui.call.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.convoxing.convoxing_customer.databinding.BottomSheetCoinPackagesBinding
import com.convoxing.convoxing_customer.ui.call.adapters.CoinPackageAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinPackagesBottomSheet(
    private val onPurchaseSuccessful: (Int) -> Unit
) : BottomSheetDialogFragment(), PurchasesUpdatedListener {

    private var _binding: BottomSheetCoinPackagesBinding? = null
    private val binding get() = _binding!!

    // Google Play Billing Client
    private lateinit var billingClient: BillingClient
    private val coinProductIds =
        listOf("com.convoxing.100coins", "com.convoxing.500coins", "com.convoxing.1000coins")
    private var productDetailsList = listOf<ProductDetails>()

    // Loading state
    private var isLoading = true
        set(value) {
            field = value
            lifecycleScope.launch {
                updateLoadingState()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCoinPackagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingClient()
        setupRecyclerView()
    }

    private fun updateLoadingState() {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvCoinPackages.visibility = View.GONE
            binding.tvNoProducts.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE

            if (productDetailsList.isEmpty()) {
                binding.rvCoinPackages.visibility = View.GONE
                binding.tvNoProducts.visibility = View.VISIBLE
            } else {
                binding.rvCoinPackages.visibility = View.VISIBLE
                binding.tvNoProducts.visibility = View.GONE
                // Update the adapter with the new product details
                (binding.rvCoinPackages.adapter as? CoinPackageAdapter)?.updateData(
                    productDetailsList
                )
            }
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(this)
            .enablePendingPurchases()
            .build()

        connectToBillingService()
    }

    private fun connectToBillingService() {
        isLoading = true

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("Billing", "Billing client connected")
                    queryAvailableProducts()
                } else {
                    Log.e("Billing", "Billing setup error: ${billingResult.responseCode}")
                    isLoading = false
                    lifecycleScope.launch {
                        Toast.makeText(
                            requireContext(),
                            "Failed to connect to Google Play Store",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("Billing", "Billing service disconnected")
                isLoading = false
            }
        })
    }

    private fun queryAvailableProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                coinProductIds.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            lifecycleScope.launch {
                isLoading = false

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList.isNotEmpty()) {
                        this@CoinPackagesBottomSheet.productDetailsList = productDetailsList
                        Log.d("Billing", "Products available: ${productDetailsList.size}")
                    } else {
                        Log.e("Billing", "No products available")
                        Toast.makeText(
                            requireContext(),
                            "No coin packages available at the moment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("Billing", "Failed to query products: ${billingResult.responseCode}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to load coin packages",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCoinPackages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CoinPackageAdapter(emptyList()) { selectedProduct ->
                launchPurchaseFlow(selectedProduct)
            }
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e("Billing", "Failed to launch billing flow: ${billingResult.responseCode}")
            lifecycleScope.launch {
                Toast.makeText(
                    requireContext(),
                    "Failed to start purchase process",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        lifecycleScope.launch {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                // Handle successful purchases
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle user cancelled
                Log.d("Billing", "User cancelled the purchase")
            } else {
                // Handle other errors
                Log.e("Billing", "Purchase failed: ${billingResult.responseCode}")
                Toast.makeText(
                    requireContext(),
                    "Purchase failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Verify purchase signature in a production app

        // If purchase is pending, update UI accordingly
        if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            lifecycleScope.launch {
                Toast.makeText(
                    requireContext(),
                    "Purchase is pending. Please check your payment method.",
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }

        // Process the purchase if it's completed
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    lifecycleScope.launch {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            // Handle successful acknowledgment
                            // Add coins to user account based on product ID
                            addCoinsToUserAccount(purchase.products[0])
                        }
                    }
                }
            } else {
                // Purchase was already acknowledged, still add coins
                lifecycleScope.launch {
                    addCoinsToUserAccount(purchase.products[0])
                }
            }
        }
    }

    private fun addCoinsToUserAccount(productId: String) {
        val coinsToAdd = when (productId) {
            "com.convoxing.100coins" -> 100
            "com.convoxing.500coins" -> 500
            "com.convoxing.1000coins" -> 1000
            else -> 0
        }

        if (coinsToAdd > 0) {
            lifecycleScope.launch(Dispatchers.IO) {
                // Call your API to add coins to user account through the callback
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Added $coinsToAdd coins to your account!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Notify parent fragment of successful purchase
                    onPurchaseSuccessful(coinsToAdd)

                    // Close the bottom sheet
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::billingClient.isInitialized) {
            billingClient.endConnection()
        }
        _binding = null
    }

    companion object {
        const val TAG = "CoinPackagesBottomSheet"
    }
}