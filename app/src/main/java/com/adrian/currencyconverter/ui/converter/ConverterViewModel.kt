package com.adrian.currencyconverter.ui.converter

import androidx.lifecycle.*
import com.adrian.currencyconverter.db.entity.Currency
import com.adrian.currencyconverter.repository.CurrencyRepository
import com.adrian.currencyconverter.repository.Resource
import com.adrian.currencyconverter.testing.OpenForTesting
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

@OpenForTesting
class ConverterViewModel @Inject constructor(private val currencyRepo: CurrencyRepository) : ViewModel() {
    private val subscriptions = CompositeDisposable()

    private val _currencyDataRequest: MutableLiveData<Resource<List<Currency>>> by lazy { prepareRequest() }
    val currencyDataRequest: LiveData<Resource<List<Currency>>>
        get() = _currencyDataRequest

    private val _conversionResults = MediatorLiveData<List<ConversionResult>>()
    val conversionResults: LiveData<List<ConversionResult>> by lazy {
        _conversionResults.addSource(selectedCurrency, { _conversionResults.value = generateRates() })
        _conversionResults.addSource(enteredAmount, { _conversionResults.value = generateRates() })
        _conversionResults
    }

    val selectedCurrency = MutableLiveData<Currency>()
    val enteredAmount = MutableLiveData<String>()

    private fun prepareRequest(): MutableLiveData<Resource<List<Currency>>> {
        val request = MutableLiveData<Resource<List<Currency>>>()
        val subscription = currencyRepo.loadCurrencies().subscribe { request.value = it }
        subscriptions.add(subscription)
        return request
    }

    private fun generateRates(): List<ConversionResult> {
        val selectedCurrency = selectedCurrency.value
        val amountStr = enteredAmount.value
        val currencies = _currencyDataRequest.value?.data
        if (currencies != null && selectedCurrency != null && !amountStr.isNullOrEmpty()) {
            val amount = BigDecimal(amountStr)
            // Convert amount to USD (which is the base currency in the app) and then convert back to target currency
            val usdValue = if (amount != BigDecimal.ZERO) {
                amount.divide(selectedCurrency.exchangeRate, 6, BigDecimal.ROUND_HALF_UP)
            } else {
                BigDecimal.ZERO
            }
            val singleUnitUsdValue = BigDecimal.ONE.divide(selectedCurrency.exchangeRate, 6, BigDecimal.ROUND_HALF_UP)
            val formatter = DecimalFormat("##,###.######")
            val results = mutableListOf<ConversionResult>()
            currencies.forEach { currency ->
                if (currency != selectedCurrency) {
                    val convertedAmount = usdValue.multiply(currency.exchangeRate).setScale(6, BigDecimal.ROUND_HALF_UP)
                    val singleUnitValue =
                        singleUnitUsdValue.multiply(currency.exchangeRate).setScale(6, BigDecimal.ROUND_HALF_UP)
                    val valueString = "${currency.currencyCode} - ${formatter.format(convertedAmount)}"
                    val singleUnitString =
                        "1 ${selectedCurrency.currencyCode} = ${formatter.format(singleUnitValue)} ${currency.currencyCode}"

                    results.add(ConversionResult(valueString, singleUnitString, currency.currencyName))
                }
            }
            return results
        }
        return listOf()
    }

    fun retry() {
        val subscription = currencyRepo.loadCurrencies().subscribe { _currencyDataRequest.value = it }
        subscriptions.add(subscription)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.dispose()
    }
}