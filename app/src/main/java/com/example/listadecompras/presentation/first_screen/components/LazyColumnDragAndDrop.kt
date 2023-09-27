package com.example.listadecompras.presentation.first_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.listadecompras.domain.ShopItem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun LazyColumnDragAndDrop(
    items: List<ShopItem>,
    modifier: Modifier,
    onReordered: (Int, Int) -> Unit,
    onSwap: (Int, Int) -> Unit,
    onRemove: (ShopItem) -> Unit,
    onToggle: (ShopItem) -> Unit,
    onItemClick: (ShopItem) -> Unit
) {
    val state = rememberReorderableLazyListState(
        onMove = { from, to -> onReordered(from.index, to.index) },
        onDragEnd = { startIndex, endIndex -> onSwap(startIndex, endIndex) }
    )

    LazyColumn(
        modifier = modifier
            .reorderable(
                state = state,

                )
            .detectReorderAfterLongPress(state),

    state = state.listState,
        contentPadding = PaddingValues(8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = items,
            key = { item -> item.hashCode() }
        ) { item ->

            ReorderableItem(
                reorderableState = state,
                key = item.hashCode()

            ) { isDragging ->

                val alpha = if (isDragging) 0.7f else 1.0f
                ShopItemSwipeable(
                    shopItem = item,
                    modifier = Modifier
                        .alpha(alpha)
                        .clickable {
                            onItemClick(item)
                        },
                    onRemove = onRemove,
                    onToggle = onToggle
                )
            }
        }
    }
}