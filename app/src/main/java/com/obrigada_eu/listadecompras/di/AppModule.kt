package com.obrigada_eu.listadecompras.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.obrigada_eu.listadecompras.data.AppDatabase
import com.obrigada_eu.listadecompras.data.ShopListDao
import com.obrigada_eu.listadecompras.data.ShopListMapper
import com.obrigada_eu.listadecompras.data.ShopListRepositoryImpl
import com.obrigada_eu.listadecompras.domain.ShopListRepository
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

    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> {
        return applicationContext.shopListDataStore
    }

    @Singleton
    @Provides
    fun provideRepository(
        dao: ShopListDao,
        mapper: ShopListMapper,
        dataStore: DataStore<Preferences>
    ): ShopListRepository {
        return ShopListRepositoryImpl(dao, mapper, dataStore)
    }

    @Singleton
    @Provides
    fun provideMapper(): ShopListMapper {
        return ShopListMapper()
    }

}