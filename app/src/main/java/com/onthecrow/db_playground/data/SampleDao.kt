package com.onthecrow.db_playground.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SampleDao {

    @Query("SELECT * FROM sampleentity")
    fun getAll(): PagingSource<Int, SampleEntity>

    @Query("SELECT * FROM sampleentity limit 1")
    suspend fun getOne(): SampleEntity?

    @Query("SELECT * FROM sampleentity limit 1 offset :index")
    suspend fun getOne(index: Int): SampleEntity?

    @Insert
    suspend fun insertAll(vararg entities: SampleEntity)

    @Update
    suspend fun update(entity: SampleEntity)

    @Delete
    suspend fun delete(entity: SampleEntity)
}