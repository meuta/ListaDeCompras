package com.example.listadecompras.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.listadecompras.R

class ShopItemActivity : AppCompatActivity() {

    private lateinit var viewModel: ShopItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

//        viewModel.errorInputName.value = false       // Not correct, because View can only subscribe to a LiveData-objects
                                                    // And react to them
                                                    // But can't insert a value into them
        //Try to change: var errorInputName: LiveData<Boolean> = MutableLiveData<Boolean>()
        //And now.. Cannot assign to 'value': the setter is protected/*protected and package*/ for synthetic extension in '<library Gradle: androidx.lifecycle:lifecycle-livedata-core:2.3.1@aar>'

    }
}