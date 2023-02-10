package cn.chitanda.app.imovie.ui.ext

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * @author: Chen
 * @createTime: 2023/2/10 18:37
 * @description:
 **/
@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(direction) + paddingValues.calculateStartPadding(direction),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = calculateEndPadding(direction) + paddingValues.calculateEndPadding(direction),
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding()
    )
}