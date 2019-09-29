package com.adrian.currencyconverter.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adrian.currencyconverter.api.CurrencyNameResponse
import com.adrian.currencyconverter.api.CurrencyService
import com.adrian.currencyconverter.api.ExchangeRatesResponse
import com.adrian.currencyconverter.db.dao.CurrencyDao
import com.adrian.currencyconverter.db.dao.TimestampDao
import com.adrian.currencyconverter.db.entity.Timestamp
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.math.BigDecimal

class CurrencyRepositoryTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
    private val timestampDao = mock(TimestampDao::class.java)
    private val currencyDao = mock(CurrencyDao::class.java)
    private val currencyService = mock(CurrencyService::class.java)
    private val repository = CurrencyRepository(currencyDao, timestampDao, currencyService)

    @Before
    fun init() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `load from network when there is no data in db`() {
        `when`(currencyDao.getCurrencyCount()).thenReturn(Single.just(0))
        `when`(currencyService.getCurrencies(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                CurrencyNameResponse(
                    success = true,
                    names = null
                )
            )
        )
        `when`(currencyService.getExchangeRates(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                ExchangeRatesResponse(
                    success = true,
                    currencies = null
                )
            )
        )
        repository.loadCurrencies()
        verify(currencyService, atLeastOnce()).getCurrencies()
        verify(currencyService, atLeastOnce()).getExchangeRates()
    }

    @Test
    fun `load from network when the data in db is older than 30 minutes`() {
        val lastUpdated = System.currentTimeMillis() - 60 * 60 * 1000L
        `when`(currencyDao.getCurrencyCount()).thenReturn(Single.just(100))
        `when`(timestampDao.getTimestampFor(ArgumentMatchers.anyString())).thenReturn(
            Single.just(Timestamp("", lastUpdated))
        )
        `when`(currencyService.getCurrencies(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                CurrencyNameResponse(
                    success = true,
                    names = null
                )
            )
        )
        `when`(currencyService.getExchangeRates(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                ExchangeRatesResponse(
                    success = true,
                    currencies = null
                )
            )
        )
        repository.loadCurrencies()
        verify(currencyService, atLeastOnce()).getCurrencies()
        verify(currencyService, atLeastOnce()).getExchangeRates()
    }

    @Test
    fun `load from db when the data in db is not older than 30 minutes`() {
        val lastUpdated = System.currentTimeMillis() - 15 * 60 * 1000L
        `when`(currencyDao.getCurrencyCount()).thenReturn(Single.just(100))
        `when`(currencyDao.getCurrencies()).thenReturn(Single.just(listOf()))
        `when`(timestampDao.getTimestampFor(ArgumentMatchers.anyString())).thenReturn(
            Single.just(Timestamp("", lastUpdated))
        )
        repository.loadCurrencies()
        verify(currencyService, never()).getCurrencies()
        verify(currencyService, never()).getExchangeRates()
        verify(currencyDao, atLeastOnce()).getCurrencies()
    }

    @Test
    fun `don't save network results to db if one of the requests fails`() {
        `when`(currencyDao.getCurrencyCount()).thenReturn(Single.just(0))
        `when`(currencyService.getCurrencies(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                CurrencyNameResponse(
                    success = true,
                    names = mutableMapOf()
                )
            )
        )
        `when`(currencyService.getExchangeRates(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                ExchangeRatesResponse(
                    success = false,
                    currencies = null
                )
            )
        )
        repository.loadCurrencies()
        verify(currencyDao, never()).insert(ArgumentMatchers.anyList())
    }

    @Test
    fun `save network results to db only if both requests passes`() {
        `when`(currencyDao.getCurrencyCount()).thenReturn(Single.just(0))
        `when`(currencyDao.insert(ArgumentMatchers.anyList())).thenReturn(Completable.complete())
        `when`(currencyService.getCurrencies(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                CurrencyNameResponse(
                    success = true,
                    names = mapOf("USD" to "United States Dollar")
                )
            )
        )
        `when`(currencyService.getExchangeRates(ArgumentMatchers.anyString())).thenReturn(
            Single.just(
                ExchangeRatesResponse(
                    success = true,
                    currencies = mapOf("USDLKR" to BigDecimal("189.91"))
                )
            )
        )
        repository.loadCurrencies()
        verify(currencyDao, atLeastOnce()).insert(ArgumentMatchers.anyList())
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}