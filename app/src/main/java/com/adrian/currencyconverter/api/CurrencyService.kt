package com.adrian.currencyconverter.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("list")
    fun getCurrencies(@Query("access_key") apiKey: String = API_KEY): Single<CurrencyNameResponse>

    @GET("live")
    fun getExchangeRates(@Query("access_key") apiKey: String = API_KEY): Single<ExchangeRatesResponse>

    companion object {
        const val API_KEY = "9499aeaa363e4eb5914702e48184a286"
    }
}