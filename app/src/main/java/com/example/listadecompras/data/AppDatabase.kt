package com.example.listadecompras.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopItemBbModel::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun shopListDao(): ShopListDao

    companion object{

        private var INSTANCE: AppDatabase? = null

        private val LOCK = Any()

        private const val DB_NAME = "shop_item.db"
// To get an instance variable, we need a context. To avoid leaking the activity context, we use application.:
        fun getInstance(application: Application): AppDatabase {
// If it already exists:
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK){
                INSTANCE?.let {
                    return it
                }
//If it is null, create a db:
                val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .build()
                INSTANCE = db
                return db       //Because INSTANCE is a nullable
            }
        }
    }
}