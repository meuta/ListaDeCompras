package com.example.listadecompras.presentation

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem.Companion.UNDEFINED_ID
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("setErrorInputName")
fun bindErrorInputName(til: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        til.context.getString(R.string.error_input_name)
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

@BindingAdapter("itemId", "setCount")
fun bindCount(editText: EditText, itemId: Int, itemCount: Double){
    val count = if (itemId == UNDEFINED_ID){
        1.0
    } else {
        itemCount
    }
    editText.setText(count.toString())
}