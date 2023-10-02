package com.example.listadecompras.di

import android.content.Context
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
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideShopListDao(db: AppDatabase): ShopListDao {
        return db.shopListDao()
    }

    @Singleton
    @Provides
    fun provideRepository(
        dao: ShopListDao, mapper: ShopListMapper
    ): ShopListRepository {
        return ShopListRepositoryImpl(dao, mapper)
    }

    @Singleton
    @Provides
    fun provideMapper(): ShopListMapper {
        return ShopListMapper()
    }

}