package com.obrigada_eu.listadecompras.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel

@Database(
    entities = [
        ShopItemDbModel::class,
        ShopListDbModel::class
    ],
    version = 12,
    autoMigrations = [
        AutoMigration (from = 11, to = 12)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopListDao(): ShopListDao
    abstract fun shopItemDao(): ShopItemDao

    companion object {

        private const val DB_NAME = "shop_item.db"

        fun getInstance(context: Context): AppDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}