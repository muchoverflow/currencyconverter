package com.adrian.currencyconverter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adrian.currencyconverter.db.dao.CurrencyDao
import com.adrian.currencyconverter.db.dao.TimestampDao
import com.adrian.currencyconverter.db.entity.Currency
import com.adrian.currencyconverter.db.entity.Timestamp

@Database(
    entities = [
        Currency::class,
        Timestamp::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    abstract fun timestampDao(): TimestampDao
}