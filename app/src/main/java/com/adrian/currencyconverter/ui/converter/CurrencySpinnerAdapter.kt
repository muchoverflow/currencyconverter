package com.adrian.currencyconverter.ui.converter

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import com.adrian.currencyconverter.db.entity.Currency
import android.widget.TextView
import android.view.ViewGroup

class CurrencySpinnerAdapter constructor(
    private var currencies: List<Currency>,
    context: Context,
    resource: Int
) : ArrayAdapter<Currency>(context, resource, currencies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        @Suppress("SetTextI18n")
        label.text = "${currencies[position].currencyCode} - ${currencies[position].currencyName}"

        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        @Suppress("SetTextI18n")
        label.text = "${currencies[position].currencyCode} - ${currencies[position].currencyName}"

        return label
    }
}