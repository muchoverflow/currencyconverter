package com.adrian.currencyconverter.api

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ExchangeRatesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("quotes") val currencies: Map<String, BigDecimal>?
)