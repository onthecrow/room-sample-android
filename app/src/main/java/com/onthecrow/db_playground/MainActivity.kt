package com.onthecrow.db_playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.onthecrow.db_playground.data.DatabaseManager
import com.onthecrow.db_playground.data.SampleRepository
import com.onthecrow.db_playground.ui.list.ListItem
import com.onthecrow.db_playground.ui.theme.DbplaygroundTheme

class MainActivity : ComponentActivity() {

    private val databaseManager by lazy { DatabaseManager(application) }
    private val sampleRepository by lazy { SampleRepository(databaseManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DbplaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isLoaded = databaseManager.prepopulatedState.collectAsState()
                    val dataState = sampleRepository.getSampleData().collectAsLazyPagingItems()
                    val listState = rememberLazyListState()

                    LaunchedEffect(Unit) {
                        databaseManager.setRangeProvider { listState.firstVisibleItemIndex..listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size }
                    }
                    LaunchedEffect(dataState) {
                        repeat(Int.MAX_VALUE) {
                            listState.animateScrollBy(
                                1000f,
                                animationSpec = tween(1000, easing = LinearEasing)
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
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
                    if (isLoaded.value.not()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.size(8.dp))
                            Text("Prepopulating DB...")
                        }
                    }
                }
            }
        }
    }
}