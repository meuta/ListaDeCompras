package com.obrigada_eu.listadecompras.di

import android.content.Context
import androidx.room.Room
import com.obrigada_eu.listadecompras.data.database.AppDatabase
import com.obrigada_eu.listadecompras.data.database.ShopListDao
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.repositories.ShopListRepositoryImpl
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

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
    fun provideShopListDao(db: AppDatabase): ShopListDao {
        return db.shopListDao()
    }

    @Singleton
    @Provides
    fun provideRepository(dao: ShopListDao, mapper: ShopListMapper): ShopListRepository {
        return ShopListRepositoryImpl(dao, mapper)
    }

    @Singleton
    @Provides
    fun provideMapper(): ShopListMapper {
        return ShopListMapper()
    }
}