package com.obrigada_eu.listadecompras.presentation

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem.Companion.UNDEFINED_ID
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
    Log.d(TAG, "BindingAdapter: errorInputName = $error ")
    til.errorIconDrawable = null
    til.error = error
}


@BindingAdapter("itemId", "setCountToEditText")
fun bindCount(editText: EditText, itemId: Int?, itemCount: Double?){
    itemId?.let {
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
fun bindUnits(editText: EditText, itemId: Int?, itemUnits: String?){
    itemId?.let {
        if (itemId != UNDEFINED_ID) {
            itemUnits?.let { editText.setText(it) }
        }
    }
}

@BindingAdapter("alterNameVisibility")
fun setAlterNameVisibility(view: View, isAlternativeName: Boolean){
//    Log.d(TAG, "setAlterNameVisibility: isAlternativeName = isAlternativeName")
    if (isAlternativeName) view.visibility = View.VISIBLE
}


@BindingAdapter("setEditTextName")
fun bindEditTextName(editText: EditText, name: String?){
//    Log.d(TAG, "bindEditTextName: editText = ${editText.id}, name = $name")
    name?.let {
        with(editText) {
            tag = TAG_ERROR_INPUT_NAME
            setText(it)
            tag = null
            setSelection(editText.text.length)
        }
    }
}


@BindingAdapter("etListNameIsChecked")
fun bindEditTextNameIsChecked(editText: EditText, isNameFromTitle: Boolean) {
    with(editText) {

        if (editText.id == R.id.et_list_name_from_title && isNameFromTitle ||
            editText.id == R.id.et_list_name_from_content && !isNameFromTitle
        ) {
            setBackgroundResource(R.color.whitish)
            setTextColor(context.getColor(R.color.grayish))
            requestFocus()
            setSelection(text.length)
        } else {
            setBackgroundResource(R.color.whitish_transparent)
            setTextColor(context.getColor(R.color.blackish_transparent))
        }
    }
}

