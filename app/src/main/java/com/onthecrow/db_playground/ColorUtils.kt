package com.onthecrow.db_playground

import androidx.compose.ui.graphics.Color
import com.onthecrow.db_playground.ui.theme.Cyan40
import com.onthecrow.db_playground.ui.theme.Green40
import com.onthecrow.db_playground.ui.theme.Lylac40
import com.onthecrow.db_playground.ui.theme.Rose40

object ColorUtils {

    private val colors = arrayOf(
        Cyan40,
        Lylac40,
        Rose40,
        Green40,
    )

    fun getRandomColor(): Int {
        return colors.indexOf(colors.random())
    }

    fun colorForIndex(index: Int): Color {
        return colors[index]
    }
}