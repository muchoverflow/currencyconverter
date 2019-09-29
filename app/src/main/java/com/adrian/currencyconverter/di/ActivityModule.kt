package com.adrian.currencyconverter.di

import com.adrian.currencyconverter.ui.converter.ConverterActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): ConverterActivity
}