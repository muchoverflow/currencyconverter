package com.adrian.currencyconverter.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrian.currencyconverter.db.entity.Currency
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currency: List<Currency>): Completable

    @Query("SELECT * FROM currency ORDER BY currencyName ASC")
    fun getCurrencies(): Single<List<Currency>>

    @Query("SELECT COUNT(*) FROM currency")
    fun getCurrencyCount(): Single<Int>
}