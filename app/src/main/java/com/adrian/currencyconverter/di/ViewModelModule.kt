package com.adrian.currencyconverter.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adrian.currencyconverter.ui.converter.ConverterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ConverterViewModel::class)
    abstract fun bindConverterViewModel(converterViewModel: ConverterViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}