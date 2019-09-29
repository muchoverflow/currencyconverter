package com.adrian.currencyconverter.repository

import com.adrian.currencyconverter.api.CurrencyNameResponse
import com.adrian.currencyconverter.api.CurrencyService
import com.adrian.currencyconverter.api.ExchangeRatesResponse
import com.adrian.currencyconverter.db.dao.CurrencyDao
import com.adrian.currencyconverter.db.dao.TimestampDao
import com.adrian.currencyconverter.db.entity.Currency
import com.adrian.currencyconverter.db.entity.Timestamp
import com.adrian.currencyconverter.testing.OpenForTesting
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val timestampDao: TimestampDao,
    private val currencyService: CurrencyService
) {
    fun loadCurrencies(): BehaviorSubject<Resource<List<Currency>>> {
        return object : NetworkBasedResource<Pair<CurrencyNameResponse, ExchangeRatesResponse>, List<Currency>>() {
            override fun loadFromDb(): Single<List<Currency>> = currencyDao.getCurrencies()

            override fun createCall(): Single<Pair<CurrencyNameResponse, ExchangeRatesResponse>> {
                val nameCall = currencyService.getCurrencies()
                val rateCall = currencyService.getExchangeRates()
                return Single.zip(nameCall, rateCall, BiFunction { nameResponse: CurrencyNameResponse, rateResponse: ExchangeRatesResponse -> Pair(nameResponse, rateResponse)})
            }

            override fun saveCallResult(item: Pair<CurrencyNameResponse, ExchangeRatesResponse>): Completable {
                val currencyNames = item.first
                val rates = item.second

                // Throw an exception if one or more request fails on the api level. This will propagate through the RxChain
                // and the UI will get updated accordingly
                if (rates.currencies == null || currencyNames.names == null)
                    throw Exception("Api response failure. Check whether you have exceeded the request quota!")

                val currenciesToInsert = mutableListOf<Currency>()
                for (currency in rates.currencies) {
                    val currencyCode = currency.key.substring(3, 6)
                    currencyNames.names[currencyCode]?.let {
                        currenciesToInsert.add(Currency(currencyCode, it, currency.value))
                    }
                }

                return currencyDao.insert(currenciesToInsert)
                    .andThen(timestampDao.insert(Timestamp(TABLE_NAME, System.currentTimeMillis())))
            }

            override fun shouldFetch(): Single<Boolean> = checkLastUpdated()

        }.apply { request() }.getSubject()
    }

    private fun checkLastUpdated(): Single<Boolean> {
        @Suppress("NoSubs")
        return currencyDao.getCurrencyCount()
            .flatMap { count ->
                if (count == 0)
                    Single.just(true)
                else
                    timestampDao.getTimestampFor(TABLE_NAME)
                        .flatMap { timestamp -> Single.just(System.currentTimeMillis() - timestamp.time > (30 * 60 * 1000L)) }
                        .onErrorReturn { true }
            }
    }

    companion object {
        private const val TABLE_NAME = "currency"
    }
}