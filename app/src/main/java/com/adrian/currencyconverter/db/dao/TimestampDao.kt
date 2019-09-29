package com.adrian.currencyconverter.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrian.currencyconverter.db.entity.Timestamp
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TimestampDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timestamp: Timestamp): Completable

    @Query("SELECT * FROM timestamp WHERE tableKey = :tableKey")
    fun getTimestampFor(tableKey: String): Single<Timestamp>
}