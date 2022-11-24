package com.example.listadecompras

import android.app.Application
import com.example.listadecompras.di.DaggerApplicationComponent

class ShopApplication : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}