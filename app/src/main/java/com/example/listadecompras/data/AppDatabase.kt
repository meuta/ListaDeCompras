package com.example.listadecompras.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopItemBbModel::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun shopListDao(): ShopListDao

    companion object{

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