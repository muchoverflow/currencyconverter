package com.adrian.currencyconverter.di

import android.app.Application
import androidx.room.Room
import com.adrian.currencyconverter.api.CurrencyService
import com.adrian.currencyconverter.db.CurrencyDatabase
import com.adrian.currencyconverter.db.dao.CurrencyDao
import com.adrian.currencyconverter.db.dao.TimestampDao
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideCurrencyService(): CurrencyService {
        return Retrofit.Builder()
            .baseUrl("http://apilayer.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CurrencyService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): CurrencyDatabase {
        return Room
            .databaseBuilder(app, CurrencyDatabase::class.java, "currencyconverter.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCurrencyDao(db: CurrencyDatabase): CurrencyDao {
        return db.currencyDao()
    }

    @Singleton
    @Provides
    fun provideTimestampDao(db: CurrencyDatabase): TimestampDao {
        return db.timestampDao()
    }
}