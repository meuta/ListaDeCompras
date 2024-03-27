package com.obrigada_eu.listadecompras.presentation

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem.Companion.UNDEFINED_ID
import com.google.android.material.textfield.TextInputLayout
import com.obrigada_eu.listadecompras.R
import kotlin.math.roundToInt

private const val TAG = "BindingAdapter"
private const val TAG_ERROR_INPUT_NAME = 101

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


@BindingAdapter("setErrorInputListName")
fun bindErrorInputListName(til: TextInputLayout, error: String?){
    Log.d("BindingAdapter", "error = $error ")
    til.errorIconDrawable = null
    til.error = error
}

//@BindingAdapter("setTextViewErrorInputListName")
//fun bindTextViewErrorInputListName(textView: TextView, isError: Boolean){
//    Log.d("BindingAdapter", "isError = $isError ")
//    val message = if (isError) {
//        textView.context.getString(R.string.error_input_list_name)
//    } else {
//        ""
//    }
//    textView.text = message
//}

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

@BindingAdapter("alterNameVisibility")
fun setAlterNameVisibility(view: View, name: String?){
    Log.d(TAG, "setAlterNameVisibility: name = $name")
    view.visibility = if (name == null) View.GONE else View.VISIBLE
}


@BindingAdapter("setEditTextName")
fun bindEditTextName(editText: EditText, name: String?){
    Log.d(TAG, "bindEditTextName: editText = ${editText.id}, name = $name")
    name?.let {
        with(editText) {
            tag = TAG_ERROR_INPUT_NAME
            setText(it)
            setSelection(editText.text.length)
            tag = null
        }
    }
}