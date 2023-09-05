package cn.chitanda.app.imovie.ui.modifier

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastFirstOrNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * @author: Chen
 * @createTime: 2023/8/31 10:58
 * @description:
 **/

@Composable
fun rememberPlayerViewGestureState(
    onHorizontalDelta: (Float) -> Unit = {},
    onLeftDelta: (Float) -> Unit = {},
    onRightDelta: (Float) -> Unit = {},
    onPressChanged: (Boolean) -> Unit = {},
    onTouch: () -> Unit = {},
): PlayerViewGestureState {
    val onHorizontalDeltaState = rememberUpdatedState(newValue = onHorizontalDelta)
    val onLeftDeltaState = rememberUpdatedState(newValue = onLeftDelta)
    val onRightDeltaState = rememberUpdatedState(newValue = onRightDelta)
    val onPressChangedState = rememberUpdatedState(newValue = onPressChanged)
    val onTouchState = rememberUpdatedState(newValue = onTouch)

    return remember(
        onHorizontalDelta, onLeftDelta, onRightDelta, onPressChanged
    ) {
        DefaultPlayerViewGestureState(
            onHorizontalDelta = onHorizontalDeltaState.value,
            onLeftDelta = onLeftDeltaState.value,
            onRightDelta = onRightDeltaState.value,
            onPressChanged = onPressChangedState.value,
            onTouch = onTouchState.value
        )
    }
}


private class DefaultPlayerViewGestureState(
    onHorizontalDelta: (Float) -> Unit,
    onLeftDelta: (Float) -> Unit,
    onRightDelta: (Float) -> Unit, onPressChanged: (Boolean) -> Unit, onTouch: () -> Unit
) : PlayerViewGestureState {
    override val orientation: Orientation = Orientation.Vertical

    private val scope = object : PlayerViewGestureScope {
        private var startEvent: GestureEvent.DragStarted? = null
        private var onPressed = false
        override fun dragBy(bounds: IntSize, delta: Offset) {
            if (startEvent != null) {
                if (startEvent!!.orientation == Orientation.Vertical) {
                    val deltaF =
                        delta.toFloat(Orientation.Vertical) / bounds.toFloat(Orientation.Vertical)
                    if (startEvent!!.startPoint.x < bounds.width / 2) {
                        onLeftDelta(deltaF)
                    } else {
                        onRightDelta(deltaF)
                    }
                } else {
                    val deltaF =
                        delta.toFloat(Orientation.Horizontal) / bounds.toFloat(Orientation.Horizontal)
                    onHorizontalDelta(
                        deltaF
                    )
                }
            } else {
                Timber.e("dragBy: failed", "startEvent is null")
            }
        }

        override fun startDrag(event: GestureEvent.DragStarted) {
            startEvent = event
        }

        override fun stopDrag() {
            startEvent = null
        }

        override fun press(pressed: Boolean) {
            if (onPressed != pressed) {
                onPressed = pressed
                onPressChanged(pressed)
            }
        }

        override fun onTouch() {
            press(false)
            onTouch()
        }

        override fun onCancel() {
            if (onPressed) {
                onPressChanged(false)
            }
        }

    }
    private val mutex = MutatorMutex()
    override suspend fun handle(
        dragPriority: MutatePriority, block: suspend PlayerViewGestureScope.() -> Unit
    ) = coroutineScope {
        mutex.mutateWith(scope, dragPriority, block)
    }
}

fun Modifier.listenPlayerViewGesture(
    state: PlayerViewGestureState,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    canDrag: (PointerInputChange) -> Boolean = { true },
    onDragStarted: suspend CoroutineScope. (Offset) -> Unit = {},
    onDragStopped: suspend CoroutineScope. () -> Unit = {},
) = this then PlayerViewGestureModifierElement(
    state, state.orientation, reverseDirection, enabled, canDrag, onDragStarted, onDragStopped
)

class PlayerViewGestureModifierElement(
    private val state: PlayerViewGestureState,
    private val orientation: Orientation,
    private val reverseDirection: Boolean,
    private val enabled: Boolean,
    private val canDrag: (PointerInputChange) -> Boolean,
    private val onDragStarted: suspend CoroutineScope. (Offset) -> Unit,
    private val onDragStopped: suspend CoroutineScope. () -> Unit,
) : ModifierNodeElement<PlayerViewGestureModifierNode>() {
    override fun InspectorInfo.inspectableProperties() {
        name = "playViewDragListener"
        properties["canDrag"] = canDrag
        properties["orientation"] = orientation
        properties["enabled"] = enabled
        properties["onDragStarted"] = onDragStarted
        properties["onDragStopped"] = onDragStopped
        properties["state"] = state
        properties["reverseDirection"] = reverseDirection
    }

    override fun create(): PlayerViewGestureModifierNode = PlayerViewGestureModifierNode(
        state, orientation, reverseDirection, enabled, canDrag, onDragStarted, onDragStopped
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayerViewGestureModifierElement) return false
        if (state != other.state) return false
        if (canDrag != other.canDrag) return false
        if (orientation != other.orientation) return false
        if (enabled != other.enabled) return false
        if (onDragStarted != other.onDragStarted) return false
        if (onDragStopped != other.onDragStopped) return false
        if (reverseDirection != other.reverseDirection) return false
        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + canDrag.hashCode()
        result = 31 * result + orientation.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + onDragStarted.hashCode()
        result = 31 * result + onDragStopped.hashCode()
        result = 31 * result + reverseDirection.hashCode()
        return result
    }

    override fun update(node: PlayerViewGestureModifierNode) {
        node.update(state, canDrag, orientation, enabled, onDragStarted, onDragStopped)
    }

}

class PlayerViewGestureModifierNode(
    private var state: PlayerViewGestureState,
    private var orientation: Orientation,
    private var reverseDirection: Boolean,
    private var enabled: Boolean,
    private var canDrag: (PointerInputChange) -> Boolean,
    private var onDragStarted: suspend CoroutineScope.(Offset) -> Unit,
    private var onDragStopped: suspend CoroutineScope.() -> Unit,
) : PointerInputModifierNode, DelegatingNode() {
    private val channel = Channel<GestureEvent>(capacity = Channel.UNLIMITED)
    private val inputNode = delegate(SuspendingPointerInputModifierNode {
        coroutineScope {
            if (!enabled) return@coroutineScope
            launch(start = CoroutineStart.UNDISPATCHED, context = Dispatchers.Default) {
                while (isActive) {
                    var event = channel.receive()
                    try {
                        if (event is GestureEvent.DragStarted) {
                            processDragStart(event)
                            state.handle(MutatePriority.UserInput) {
                                while (event !is GestureEvent.DragStopped && event !is GestureEvent.Cancelled) {
                                    (event as? GestureEvent.DragDelta)?.let {
                                        dragBy(it.bounds, it.delta)
                                    }
                                    event = channel.receive()
                                }
                            }

                            if (event is GestureEvent.DragStopped || event is GestureEvent.Cancelled) {
                                processDragStopOrCancel()
                            }
                        } else if (event is GestureEvent.OnTouch) {
                            state.handle(MutatePriority.UserInput) {
                                onTouch()
                            }
                        } else {
                            when (event) {
                                is GestureEvent.PressStarted -> {
                                    state.handle(MutatePriority.UserInput) {
                                        press(true)
                                    }
                                }

                                is GestureEvent.PressStopped -> {
                                    state.handle(MutatePriority.UserInput) {
                                        press(false)
                                    }
                                }

                                is GestureEvent.Cancelled -> {
                                    state.handle(MutatePriority.UserInput) {
                                        press(false)
                                    }
                                }

                                else -> {}
                            }
                        }
                    } catch (e: CancellationException) {
                        processDragStopOrCancel()
                    }
                }
            }
            launch(start = CoroutineStart.UNDISPATCHED, context = Dispatchers.Default) {
                awaitPointerEventScope {
                    while (isActive) {
                        with(awaitDownAndSlop(canDrag, channel)) {
                            first?.let { change ->
                                var dragSuccess = false
                                try {
                                    dragSuccess = awaitDrag(
                                        change, second, channel, reverseDirection
                                    )
                                } catch (e: CancellationException) {
                                    dragSuccess = false
                                    if (!isActive) throw e
                                } finally {
                                    channel.trySend(
                                        if (dragSuccess) {
                                            GestureEvent.DragStopped
                                        } else {
                                            GestureEvent.Cancelled
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    })

    private suspend fun CoroutineScope.processDragStart(event: GestureEvent.DragStarted) {
        state.handle(MutatePriority.UserInput) {
            startDrag(event)
        }
        onDragStarted(this, event.startPoint)
    }

    private suspend fun CoroutineScope.processDragStopOrCancel() {
        onDragStopped(this)
    }

    override fun onCancelPointerInput() {
        inputNode.onCancelPointerInput()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent, pass: PointerEventPass, bounds: IntSize
    ) {
        inputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    fun update(
        state: PlayerViewGestureState,
        canDrag: (PointerInputChange) -> Boolean,
        orientation: Orientation,
        enabled: Boolean,
        onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
        onDragStopped: suspend CoroutineScope.() -> Unit,
    ) {
        var resetPointerInputHandling = false
        if (this.state != state) {
            this.state = state
            resetPointerInputHandling = true
        }
        this.canDrag = canDrag
        if (this.orientation != orientation) {
            this.orientation = orientation
            resetPointerInputHandling = true
        }
        if (this.enabled != enabled) {
            this.enabled = enabled
            resetPointerInputHandling = true
        }
        this.onDragStarted = onDragStarted
        this.onDragStopped = onDragStopped

        if (resetPointerInputHandling) {
            inputNode.resetPointerInputHandler()
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitLongPress(
    channel: SendChannel<GestureEvent>, down: PointerInputChange
) {
    awaitLongPressOrCancellation(down.id)?.let { point ->
        channel.trySend(GestureEvent.PressStarted(point.position))
        try {
            awaitPointerUp(point.id)
        } catch (_: CancellationException) {
        } finally {
            channel.trySend(GestureEvent.PressStopped)
        }
    } ?: run {
        channel.trySend(GestureEvent.OnTouch)
    }
}

private suspend fun AwaitPointerEventScope.awaitPointerUp(pointerId: PointerId) {
    if (currentEvent.isPointerUp(pointerId)) {
        return
    }
    while (true) {
        val event = awaitPointerEvent()
        val pressEvent = event.changes.fastFirstOrNull { it.id == pointerId } ?: return
        if (pressEvent.isConsumed || pressEvent.changedToUpIgnoreConsumed()) {
            return
        }
    }
}


private suspend fun AwaitPointerEventScope.awaitDownAndSlop(
    canDrag: (PointerInputChange) -> Boolean, channel: SendChannel<GestureEvent>,
): Pair<PointerInputChange?, Offset> {
    val down = awaitFirstDown(requireUnconsumed = true, pass = PointerEventPass.Main)
    return if (!canDrag(down)) {
        null to Offset.Zero
    } else {
        var initialDelta = Offset.Zero
        val afterSlopResult = awaitTouchSlopOrCancellation(down.id) { event, offset ->
            if (offset.x.absoluteValue > 1 || offset.y.absoluteValue > 1) {
                event.consume()
                initialDelta = offset
            }
        }
        if (afterSlopResult == null) {
            val pointer: PointerInputChange =
                if (down.isConsumed || down.changedToUpIgnoreConsumed()) {
                    awaitFirstDown()
                } else {
                    down
                }
            awaitLongPress(channel, pointer)
        }
        afterSlopResult to initialDelta
    }
}

private suspend fun AwaitPointerEventScope.awaitDrag(
    startEvent: PointerInputChange,
    initialDelta: Offset,
    channel: SendChannel<GestureEvent>,
    reverseDirection: Boolean
): Boolean {

    val xSign = sign(startEvent.position.x)
    val ySign = sign(startEvent.position.y)
    val adjustedStart = startEvent.position - Offset(initialDelta.x * xSign, initialDelta.y * ySign)
    channel.trySend(
        GestureEvent.DragStarted(
            adjustedStart,
            if (initialDelta.y.absoluteValue > initialDelta.x.absoluteValue) Orientation.Vertical else Orientation.Horizontal,
        )
    )

    channel.trySend(
        GestureEvent.DragDelta(
            size, if (reverseDirection) initialDelta * -1f else initialDelta
        )
    )
    return drag(startEvent.id) { event ->
        if (!event.changedToUpIgnoreConsumed()) {
            val delta = event.positionChange()
            event.consume()
            channel.trySend(
                GestureEvent.DragDelta(
                    size, if (reverseDirection) delta * -1f else delta
                )
            )
        }
    }

}


sealed class GestureEvent {
    class DragStarted(val startPoint: Offset, val orientation: Orientation) : GestureEvent()
    data object DragStopped : GestureEvent()

    class PressStarted(val startPoint: Offset) : GestureEvent()
    data object PressStopped : GestureEvent()
    data object OnTouch : GestureEvent()
    data object Cancelled : GestureEvent()
    class DragDelta(val bounds: IntSize, val delta: Offset) : GestureEvent()
}

interface PlayerViewGestureState {
    val orientation: Orientation
    suspend fun handle(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend PlayerViewGestureScope.() -> Unit
    )

}

interface PlayerViewGestureScope {

    fun dragBy(bounds: IntSize, delta: Offset)

    fun startDrag(event: GestureEvent.DragStarted) {}

    fun stopDrag() {}

    fun press(pressed: Boolean) {}

    fun onTouch() {}

    fun onCancel() {}

}

private fun Offset.toFloat(orientation: Orientation) =
    if (orientation == Orientation.Vertical) this.y else this.x

private fun IntSize.toFloat(orientation: Orientation) =
    if (orientation == Orientation.Vertical) this.height else this.width


private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true