package com.example.listadecompras.presentation.first_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.listadecompras.domain.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopItemSwipeable(
    shopItem: ShopItem,
    modifier: Modifier,
    onRemove: (ShopItem) -> Unit,
    onToggle: (ShopItem) -> Unit
    ) {
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(shopItem)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                show = false
                true
            }
            else false
        },
        positionalThreshold = { distance -> distance * 0.7f  }
    )
    AnimatedVisibility(
        visible = show,
        exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = modifier,
            background = {

                val color by animateColorAsState(
                    when (dismissState.dismissDirection) {
                        DismissDirection.StartToEnd -> MaterialTheme.colorScheme.onError
                        DismissDirection.EndToStart -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.background
                    }, label = ""
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                )
            },
            dismissContent = {
                ShopItemCard(
                    shopItem = shopItem
                )
            },

        )
    }

    LaunchedEffect(show) {
        if (!show) {
            when (dismissState.dismissDirection ){
                DismissDirection.StartToEnd -> {
                    onRemove(currentItem)
                }
                DismissDirection.EndToStart -> {
                    onToggle(currentItem)
                }
                else -> {}
            }
        }
    }
}