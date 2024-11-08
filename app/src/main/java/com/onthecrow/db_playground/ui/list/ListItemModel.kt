package com.onthecrow.db_playground.ui.list

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.onthecrow.db_playground.ColorUtils
import com.onthecrow.db_playground.R
import com.onthecrow.db_playground.data.SampleEntity
import com.onthecrow.db_playground.ui.theme.Grey
import com.onthecrow.db_playground.ui.theme.Grey10
import com.onthecrow.db_playground.ui.theme.LightBlue
import java.text.SimpleDateFormat
import java.util.Locale

data class ListItemModel(
    val uid: Int,
    val name: String,
    val date: String,
    val text: String,
    @DrawableRes val readIcon: Int,
    val readIconColor: Color,
    val backgroundColor: Color,
) {
    companion object {
        private val dateFormat = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.getDefault())

        fun fromSampleEntity(sampleEntity: SampleEntity) = ListItemModel(
            uid = sampleEntity.uid,
            name = "${sampleEntity.firstName} ${sampleEntity.lastName}",
            date = dateFormat.format(sampleEntity.date),
            text = sampleEntity.text,
            readIcon = if (sampleEntity.isRead) R.drawable.ic_read else R.drawable.ic_not_read,
            readIconColor = if (sampleEntity.isRead) LightBlue else Grey,
            backgroundColor = if (sampleEntity.color == null) Grey10 else ColorUtils.colorForIndex(sampleEntity.color)
        )
    }
}
