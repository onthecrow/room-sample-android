package com.onthecrow.db_playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.onthecrow.db_playground.data.DatabaseManager
import com.onthecrow.db_playground.data.SampleRepository
import com.onthecrow.db_playground.ui.list.ListItem
import com.onthecrow.db_playground.ui.theme.DbplaygroundTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val databaseManager by lazy { DatabaseManager(application) }
    private val sampleRepository by lazy { SampleRepository(databaseManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DbplaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val dataState = sampleRepository.getSampleData().collectAsLazyPagingItems()
                    val listState = rememberLazyListState()

                    LaunchedEffect(Unit) {
                        databaseManager.setRangeProvider { listState.firstVisibleItemIndex .. listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
                        launch {
                            repeat(Int.MAX_VALUE) {
                                listState.animateScrollBy(1000f, animationSpec = tween(1000, easing = LinearEasing))
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = dataState.itemCount,
                            key = dataState.itemKey { it.uid }
                        ) { index ->
                            dataState[index]?.let { repo ->
                                ListItem(repo)
                            }
                        }
                    }
                }
            }
        }
    }
}