package com.example.listadecompras.presentation.shop_item_screen


import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId

@Composable
fun ShopItemEditPane(
    modifier: Modifier,
    itemName: String,
    itemCount: String,
    showErrorName: Boolean,
    showErrorCount: Boolean,
    onNameChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }

    ConstraintLayout(
        constraints(orientation),
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState(), reverseScrolling = true)
    ) {

        ItemInputField(

            modifier = Modifier
                .padding(all = 6.dp)
                .padding(bottom = 0.dp)
                .layoutId("textName"),
            label = { Text(text = "Name") },
            text = itemName,
            onTextChange = onNameChange,
            errorText = {
                if (showErrorName) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "please enter a name",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            showError = showErrorName
        )

        ItemInputField(

            modifier = Modifier
                .padding(all = 6.dp)
                .padding(top = 0.dp)
                .padding(bottom = 0.dp)
                .layoutId("textCount"),
            label = { Text(text = "Count") },
            text = itemCount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            onTextChange = onCountChange,
            errorText = {
                if (showErrorCount) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "please enter a count",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            showError = showErrorCount
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(all = 6.dp)
                .padding(top = 0.dp)
                .layoutId("button"),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FloatingActionButtonDefaults.containerColor),
            onClick = onClick
        )
        {
            Text(
                modifier = Modifier
                    .padding(5.dp),
                color = Color.White,
                text = "SAVE"
            )
        }
    }
}


private fun constraints(orientation: String): ConstraintSet {
    return ConstraintSet {
        val textName = createRefFor("textName")
        val textCount = createRefFor("textCount")
        val button = createRefFor("button")
        if (orientation == "Portrait") {

            constrain(textName) {
                linkTo(parent.top, textCount.top)
                linkTo(parent.start, parent.end)
            }
            constrain(textCount) {
                linkTo(textName.bottom, button.top)
                linkTo(parent.start, parent.end)
                width = Dimension.fillToConstraints
            }
            constrain(button) {

                linkTo(textCount.bottom, parent.bottom, bias = 1f)
                linkTo(parent.start, parent.end)
            }

            createVerticalChain(textName, textCount, chainStyle = ChainStyle.Packed)

        } else {

            constrain(textName) {
                linkTo(parent.top, button.top)
                linkTo(parent.start, textCount.start)
                width = Dimension.fillToConstraints
            }
            constrain(textCount) {
                linkTo(textName.top, textName.bottom)
                linkTo(textName.end, parent.end)
                width = Dimension.fillToConstraints
            }
            constrain(button) {

                linkTo(textCount.bottom, parent.bottom, bias = 1f)
                linkTo(parent.start, parent.end)
            }


            createHorizontalChain(textName, textCount, chainStyle = ChainStyle.Packed)
            createVerticalChain(textName, button, chainStyle = ChainStyle.Packed)

        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun PreviewShopItemEditPane() {
    ShopItemEditPane(
        Modifier.fillMaxSize(),
        itemName = "pesto",
        itemCount = "1.9",
        showErrorName = false,
        showErrorCount = false,
        onNameChange = {},
        onCountChange = {},
        onClick = {}
    )
}