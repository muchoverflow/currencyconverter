package com.adrian.currencyconverter.ui.converter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adrian.currencyconverter.R
import com.adrian.currencyconverter.databinding.ActivityConverterBinding
import com.adrian.currencyconverter.ui.common.GridItemOffsetDecoration
import com.adrian.currencyconverter.ui.common.RetryCallback
import dagger.android.AndroidInjection
import javax.inject.Inject

class ConverterActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var converterViewModel: ConverterViewModel
    private lateinit var binding: ActivityConverterBinding
    private val ratesRvAdapter = ConversionRatesRvAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        converterViewModel = ViewModelProvider(this, viewModelFactory)
            .get(ConverterViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_converter);
        binding.lifecycleOwner = this
        initViewsAndBindings()
    }

    private fun initViewsAndBindings() {
        binding.rvRates.layoutManager = GridLayoutManager(this, 3)
        binding.rvRates.addItemDecoration(GridItemOffsetDecoration(resources.getDimensionPixelSize(R.dimen.grid_space)))
        binding.rvRates.adapter = ratesRvAdapter
        binding.callback = object : RetryCallback {
            override fun retry() = converterViewModel.retry()
        }
        binding.resource = converterViewModel.currencyDataRequest
        binding.currency = converterViewModel.selectedCurrency
        binding.amount = converterViewModel.enteredAmount
        converterViewModel.conversionResults.observe(this, Observer { ratesRvAdapter.submitList(it) })
        converterViewModel.currencyDataRequest.observe(this, Observer { result ->
            result.data?.let {
                val spinnerAdapter = CurrencySpinnerAdapter(it, this, android.R.layout.simple_spinner_item)
                    .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                binding.currencyAdapter = spinnerAdapter
            }
        })
    }
}
