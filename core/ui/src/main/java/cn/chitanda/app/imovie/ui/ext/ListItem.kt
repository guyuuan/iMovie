package cn.chitanda.app.imovie.ui.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * @author: Chen
 * @createTime: 2023/3/29 16:12
 * @description:
 **/
@Composable
fun ListItem(
    modifier: Modifier,
    onClick: (() -> Unit)? = null,
    title: String,
    subTitle: String? = null,
    showSuffix: Boolean = true,
    showPrefix: Boolean = true,
    suffix: @Composable (BoxScope.() -> Unit)? = null,
    prefix: @Composable (BoxScope.() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    subTitleStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = (onClick?.let { Modifier.clickable(onClick = it) }
            ?: Modifier) then Modifier
            .height(IntrinsicSize.Min)
            .padding(contentPadding) then modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showPrefix && prefix != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) { prefix.invoke(this) }
        }
        Column(
            modifier = Modifier.weight(8f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(text = title, style = titleStyle)
            if (subTitle != null) {
                Text(text = subTitle, style = subTitleStyle)
            }
        }
        if (showSuffix) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                suffix?.invoke(this) ?: Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "next page"
                )
            }
        }
    }
}