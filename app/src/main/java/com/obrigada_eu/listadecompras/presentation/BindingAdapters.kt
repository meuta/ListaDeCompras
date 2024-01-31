package com.obrigada_eu.listadecompras.presentation

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem.Companion.UNDEFINED_ID
import com.google.android.material.textfield.TextInputLayout
import com.obrigada_eu.listadecompras.R
import kotlin.math.roundToInt

@BindingAdapter("setErrorInputName")
fun bindErrorInputName(til: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        til.context.getString(R.string.error_input_name)
    } else {
        null
    }
    til.error = message
}

@BindingAdapter("setErrorInputListName")
fun bindErrorInputListName(til: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        til.context.getString(R.string.error_input_list_name)
    } else {
        null
    }
    til.error = message
}

@BindingAdapter("setErrorInputCount")
fun bindErrorInputCount(til: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        til.context.getString(R.string.error_input_count)
    } else {
        null
    }
    til.error = message
}

@BindingAdapter("itemId", "setCountToEditText")
fun bindCount(editText: EditText, itemId: Int, itemCount: Double?){
    if (itemId != UNDEFINED_ID){
        itemCount?.let {
            if (it.rem(1).equals(0.0)) {
                editText.setText(it.roundToInt().toString())
            } else {
                editText.setText(it.toString())
            }
        }
    }
}

@BindingAdapter("setCount")
fun bindCount(textView: TextView, itemCount: Double?) {
    if (itemCount == null) {
        textView.text = ""
    } else if (itemCount.rem(1).equals(0.0)) {
        textView.text = itemCount.roundToInt().toString()
    } else {
        textView.text = itemCount.toString()
    }
}


@BindingAdapter("setUnits")
fun bindUnits(textView: TextView, itemUnits: String?) {
    textView.text = itemUnits ?: ""
}

@BindingAdapter("itemId", "setUnitsToEditText")
fun bindUnits(editText: EditText, itemId: Int, itemUnits: String?){
    if (itemId != UNDEFINED_ID) {
        itemUnits?.let { editText.setText(it) }
    }
}