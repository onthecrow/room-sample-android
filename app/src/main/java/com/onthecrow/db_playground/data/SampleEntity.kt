package com.onthecrow.db_playground.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SampleEntity(
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "color") val color: Int?,
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
)