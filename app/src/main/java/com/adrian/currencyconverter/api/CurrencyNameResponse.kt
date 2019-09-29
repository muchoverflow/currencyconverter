package com.adrian.currencyconverter.api

import com.google.gson.annotations.SerializedName

data class CurrencyNameResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("currencies") val names: Map<String, String>?
)