package com.adrian.currencyconverter.ui.converter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.adrian.currencyconverter.R
import com.adrian.currencyconverter.databinding.ResultItemBinding
import com.adrian.currencyconverter.ui.common.DataBoundViewHolder

class ConversionRatesRvAdapter : ListAdapter<ConversionResult, DataBoundViewHolder<ResultItemBinding>>(
    object : DiffUtil.ItemCallback<ConversionResult>() {
        override fun areItemsTheSame(oldItem: ConversionResult, newItem: ConversionResult): Boolean {
            return oldItem.value == newItem.value
        }

        override fun areContentsTheSame(oldItem: ConversionResult, newItem: ConversionResult): Boolean {
            return oldItem.singleUnitValue == newItem.singleUnitValue
                    && oldItem.currencyName == newItem.currencyName
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<ResultItemBinding> {
        val binding = createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<ResultItemBinding>, position: Int) {
        bind(holder.binding, getItem(position))
        holder.binding.executePendingBindings()
    }

    private fun createBinding(parent: ViewGroup): ResultItemBinding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.result_item,
        parent,
        false
    )

    private fun bind(binding: ResultItemBinding, item: ConversionResult) {
        binding.result = item
    }
}