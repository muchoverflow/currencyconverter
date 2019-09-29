package com.adrian.currencyconverter.binding

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.adrian.currencyconverter.db.entity.Currency
import com.adrian.currencyconverter.ui.converter.CurrencySpinnerAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter(value = ["currencyAdapter"], requireAll = false)
    fun setCurrencies(spinner: Spinner, adapter: CurrencySpinnerAdapter?) {
        spinner.adapter = adapter
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedCurrency", event = "selectedCurrencyAttrChanged")
    fun getCurrentCurrency(spinner: Spinner): Currency {
        if (spinner.adapter is CurrencySpinnerAdapter) {
            return spinner.selectedItem as Currency
        }
        throw UnsupportedOperationException("The adapter must be a CurrencySpinnerAdapter")
    }

    @JvmStatic
    @BindingAdapter(value = ["selectedCurrencyAttrChanged"], requireAll = false)
    fun currencyChanged(spinner: Spinner, newTextAttrChanged: InverseBindingListener) {
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                newTextAttrChanged.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                newTextAttrChanged.onChange()
            }
        }
        spinner.onItemSelectedListener = listener
    }

    @JvmStatic
    @BindingAdapter("selectedCurrency")
    fun bindCurrencyValue(spinner: Spinner, newSelectedValue: Currency?) {
        if (newSelectedValue == null) return
        val adapter = spinner.adapter
        if (adapter is CurrencySpinnerAdapter) {
            val index = adapter.getPosition(newSelectedValue)
            spinner.setSelection(index)
        }
    }
}