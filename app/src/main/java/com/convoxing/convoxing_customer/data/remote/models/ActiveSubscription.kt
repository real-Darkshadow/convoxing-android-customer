package com.convoxing.convoxing_customer.data.remote.models

data class ActiveSubscription(
    val auto_renewal_status: String,
    val current_period_ends_at: Long,
    val current_period_starts_at: Long,
    val id: String,
    val management_url: String,
    val product_id: String,
    val status: String,
    val store: String,
    val isFreeSessionsLeft: Boolean
)