package com.adrian.currencyconverter.ui.converter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adrian.currencyconverter.db.entity.Currency
import com.adrian.currencyconverter.repository.CurrencyRepository
import com.adrian.currencyconverter.repository.Resource
import com.adrian.currencyconverter.util.mock
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.math.BigDecimal

@RunWith(JUnit4::class)
class ConverterViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
    private val repository = mock(CurrencyRepository::class.java)
    private lateinit var viewModel: ConverterViewModel

    @Before
    fun init() {
        viewModel = ConverterViewModel(repository)
    }

    @Test
    fun `load from repository on observation`() {
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.loading())
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.currencyDataRequest.observeForever(mock())
        verify(repository, times(1)).loadCurrencies()
    }

    @Test
    fun retry() {
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.loading())
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.currencyDataRequest.observeForever(mock())
        viewModel.retry()
        verify(repository, times(2)).loadCurrencies()
    }

    @Test
    fun `ensure repo is called only once even when there are multiple subscriptions`() {
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.loading())
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.currencyDataRequest.observeForever(mock()) // observer 1
        viewModel.currencyDataRequest.observeForever(mock()) // observer 2
        verify(repository, times(1)).loadCurrencies()
    }

    @Test
    fun `ensure calculation does not occur when amount and currency are not set`() {
        val observer = mock<Observer<List<ConversionResult>>>()
        viewModel.conversionResults.observeForever(observer)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `ensure calculation occur when amount is changed`() {
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.success(listOf()))
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.enteredAmount.value = "12"
        viewModel.conversionResults.observeForever(mock())
        assert(viewModel.conversionResults.value != null)
    }

    @Test
    fun `ensure calculation occurs when currency is changed`() {
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.success(listOf()))
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.selectedCurrency.value =
            Currency(currencyCode = "LKR", currencyName = "Sri Lanka Rupee", exchangeRate = BigDecimal("181.89"))
        viewModel.conversionResults.observeForever(mock())
        assert(viewModel.conversionResults.value != null)
    }

    @Test
    fun `ensure correct conversion is performed`() {
        val result = listOf(
            Currency(currencyCode = "LKR", currencyName = "Sri Lanka Rupee", exchangeRate = BigDecimal("0.5"))
        )
        val publisher = BehaviorSubject.createDefault<Resource<List<Currency>>>(Resource.success(result))
        `when`(repository.loadCurrencies()).thenReturn(publisher)
        viewModel.enteredAmount.value = "10"
        viewModel.selectedCurrency.value =
            Currency(currencyCode = "USD", currencyName = "United States Dollar", exchangeRate = BigDecimal.ONE)
        viewModel.conversionResults.observeForever(mock())
        assert(viewModel.conversionResults.value?.first()?.value == "LKR - 5")
    }
}