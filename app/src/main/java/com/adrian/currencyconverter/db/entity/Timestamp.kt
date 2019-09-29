package com.adrian.currencyconverter.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Timestamp(
    @PrimaryKey val tableKey: String,
    val time: Long
)