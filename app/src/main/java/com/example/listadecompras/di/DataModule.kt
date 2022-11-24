package com.example.listadecompras.di

import android.app.Application
import com.example.listadecompras.data.AppDatabase
import com.example.listadecompras.data.ShopListDao
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.ShopListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: ShopListRepositoryImpl): ShopListRepository

    companion object{

        @ApplicationScope
        @Provides
        fun provideShopListDao(
            application: Application
        ):ShopListDao{
            return AppDatabase.getInstance(application).shopListDao()
        }
    }
}