package com.example.listadecompras.di

import android.content.Context
import androidx.room.Room
import com.example.listadecompras.data.AppDatabase
import com.example.listadecompras.data.ShopListDao
import com.example.listadecompras.data.ShopListMapper
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.ShopListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Singleton
    @Provides
    fun provideShopListDao(@ApplicationContext context: Context): ShopListDao {
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            AppDatabase::class.java
        ).build().shopListDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            AppDatabase::class.java
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepository(db: AppDatabase, mapper: ShopListMapper): ShopListRepository {
        return ShopListRepositoryImpl(db.shopListDao(), mapper)
    }

    @Singleton
    @Provides
    fun provideMapper(): ShopListMapper {
        return ShopListMapper()
    }
}