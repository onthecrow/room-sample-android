package com.onthecrow.db_playground.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.onthecrow.db_playground.ui.list.ListItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SampleRepository(
    private val databaseManager: DatabaseManager
) {
    fun getSampleData(): Flow<PagingData<ListItemModel>> {
        return Pager(PagingConfig(pageSize = 20)) {
            databaseManager.getData()
        }.flow
            .map { pagingData ->
                pagingData.map { entity ->
                    ListItemModel.fromSampleEntity(entity)
                }
            }
    }
}
