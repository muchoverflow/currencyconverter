package com.adrian.currencyconverter.db

import androidx.room.TypeConverter
import java.math.BigDecimal

object CurrencyTypeConverter {
    @TypeConverter
    @JvmStatic
    fun stringToBigDecimal(data: String?): BigDecimal? = data?.let { BigDecimal(it) }

    @TypeConverter
    @JvmStatic
    fun bigDecimalToString(data: BigDecimal?): String? = data?.toString()
}
