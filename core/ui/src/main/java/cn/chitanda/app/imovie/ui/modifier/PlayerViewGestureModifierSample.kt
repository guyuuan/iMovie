package cn.chitanda.app.imovie.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author: Chen
 * @createTime: 2023/9/1 18:02
 * @description:
 **/

@Preview
@Composable
fun PlayViewGestureSample() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        var progress by remember {
            mutableFloatStateOf(0f)
        }
        var press by remember {
            mutableStateOf(false)
        }
        var left by remember {
            mutableFloatStateOf(0f)
        }
        var right by remember {
            mutableFloatStateOf(0f)
        }
        CompositionLocalProvider(LocalTextStyle provides LocalTextStyle.current.copy(Color.White)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .listenPlayerViewGesture(
                        rememberPlayerViewGestureState(
                            onHorizontalDelta = {
                                progress = (progress + it / 2).coerceIn(0f, 1f)
                            },
                            onPressChanged = {
                                press = it
                            },
                            onLeftDelta = {
                                left = (left + -(it)).coerceIn(0f, 1f)
                            },
                            onRightDelta = {
                                right = (right + -(it)).coerceIn(0f, 1f)
                            },
                        )
                    )
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            left.toString()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            right.toString()
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(text = "isPressed = $press")
                    Text(text = "progress = ${progress.times(100)}%")
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    }
}