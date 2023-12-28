package com.obrigada_eu.listadecompras.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.obrigada_eu.listadecompras.data.database.AppDatabase
import com.obrigada_eu.listadecompras.data.database.ShopItemDao
import com.obrigada_eu.listadecompras.data.database.ShopListDao
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.repositories.ShopListRepositoryImpl
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItemRepository
import com.obrigada_eu.listadecompras.data.repositories.ShopItemRepositoryImpl
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



private const val SHOP_LIST_PREFERENCES_NAME = "shop_list_preferences"

val Context.shopListDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SHOP_LIST_PREFERENCES_NAME
)

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
    fun provideShopItemDao(db: AppDatabase): ShopItemDao {
        return db.shopItemDao()
    }

    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> {
        return applicationContext.shopListDataStore
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext


    @Singleton
    @Provides
    fun provideShopListRepository(
        shopListDao: ShopListDao,
        mapper: ShopListMapper,
        dataStore: DataStore<Preferences>,
        context: Context
    ): ShopListRepository {
        return ShopListRepositoryImpl(shopListDao, mapper, dataStore, context)
    }

    @Singleton
    @Provides
    fun provideShopItemRepository(
        shopItemDao: ShopItemDao,
        mapper: ShopListMapper
    ): ShopItemRepository {
        return ShopItemRepositoryImpl(shopItemDao, mapper)
    }

    @Singleton
    @Provides
    fun provideMapper(): ShopListMapper {
        return ShopListMapper()
    }

}