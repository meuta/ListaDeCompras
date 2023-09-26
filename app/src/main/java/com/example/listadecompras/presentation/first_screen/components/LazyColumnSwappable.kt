package com.example.listadecompras.presentation.first_screen.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.listadecompras.domain.ShopItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnSwappable(
    items: List<ShopItem>,
    modifier: Modifier,
    onSwap: (Int, Int) -> Unit,
    onRemove: (ShopItem) -> Unit,
    onToggle: (ShopItem) -> Unit,
    onItemClick: (ShopItem) -> Unit
) {
    var overscrollJob by remember { mutableStateOf<Job?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        onSwap(fromIndex, toIndex)
    }

    LazyColumn(
        modifier = modifier
            .pointerInput(dragDropState) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropState.onDrag(offset = offset)

                        if (overscrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress

                        dragDropState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overscrollJob =
                                    scope.launch {
                                        dragDropState.state.animateScrollBy(
                                            it * 1.3f, tween(easing = FastOutLinearInEasing)
                                        )
                                    }
                            }
                            ?: run { overscrollJob?.cancel() }
                    },
                    onDragStart = { offset -> dragDropState.onDragStart(offset) },
                    onDragEnd = {
                        dragDropState.onDragInterrupted()
                        overscrollJob?.cancel()
                    },
                    onDragCancel = {
                        dragDropState.onDragInterrupted()
                        overscrollJob?.cancel()
                    }
                )
            },
        state = listState,
        contentPadding = PaddingValues(8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.hashCode() }
        ) { index, item ->

            DraggableItem(
                modifier = Modifier.clickable {
                    onItemClick(item)
                },
                dragDropState = dragDropState,
                index = index
            ) { isDragging ->
                val alpha = if (isDragging) 0.7f else 1.0f
                ShopItemSwipeable(
                    shopItem = item,
                    modifier = Modifier.alpha(alpha),
                    onRemove = onRemove,
                    onToggle = onToggle
                )
            }
        }
    }
}