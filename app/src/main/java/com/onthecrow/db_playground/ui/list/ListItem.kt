package com.onthecrow.db_playground.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(listItemModel: ListItemModel) {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = listItemModel.backgroundColor)
    ) {
        Column(Modifier.padding(8.dp)) {
            Row {
                Text(listItemModel.name)
                Spacer(Modifier.size(8.dp))
                Text(listItemModel.date)
            }
            Text(listItemModel.text)
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(listItemModel.readIcon),
                alignment = Alignment.CenterEnd,
                colorFilter = ColorFilter.tint(listItemModel.readIconColor),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun ListItemPreview() {
    ListItem(ListItemModel(1, "", "", "", 0, Color.Unspecified, Color.Unspecified))
}