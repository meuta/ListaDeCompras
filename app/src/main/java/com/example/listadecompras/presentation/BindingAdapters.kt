package com.example.listadecompras.presentation

import androidx.databinding.BindingAdapter
import com.example.listadecompras.R
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

