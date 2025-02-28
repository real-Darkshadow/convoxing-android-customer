package com.convoxing.convoxing_customer.data.remote.models.summary

data class CommonError(
    val category: String,
    val count: Int,
    val percentage: String
)