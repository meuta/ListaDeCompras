package com.example.listadecompras.presentation.shop_list_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listadecompras.domain.ShopItem

@Composable
fun ShopItemCard(
    shopItem: ShopItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box (modifier = Modifier
            .background(FloatingActionButtonDefaults.containerColor),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(FloatingActionButtonDefaults.containerColor)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .width(0.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    text = shopItem.name
                )
                Text(
                    modifier = Modifier
                        .wrapContentWidth(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    text = shopItem.count.toString()
                )

            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewShopItemCard() {
    ShopItemCard(
        modifier = Modifier.fillMaxSize(),
        shopItem = ShopItem("gelo", 2.7, true)
    )
}