package com.adrian.currencyconverter.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.adrian.currencyconverter.db.CurrencyTypeConverter
import java.math.BigDecimal

@Entity
@TypeConverters(CurrencyTypeConverter::class)
data class Currency(
    @PrimaryKey val currencyCode: String,
    val currencyName: String,
    val exchangeRate: BigDecimal
)