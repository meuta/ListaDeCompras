package com.obrigada_eu.listadecompras.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel

@Database(
    entities = [
        ShopItemDbModel::class,
        ShopListDbModel::class
    ],
    version = 14,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopListDao(): ShopListDao
    abstract fun shopItemDao(): ShopItemDao


    companion object {

        private const val DB_NAME = "shop_item.db"


        private val migration11to14 = object : Migration(11, 14) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    val c = db.query("SELECT * FROM shop_lists")
                    c.use {
                        createNewShopListsTable(db)
                        if (c.moveToFirst()) {
                            while (!c.isAfterLast) {
                                val cv = ContentValues()
                                cv.put("id", c.getInt(c.getColumnIndex("id")))
                                cv.put("shop_list_name", c.getString(c.getColumnIndex("shop_list_name")))
                                cv.put("shop_list_enabled", c.getInt(c.getColumnIndex("shop_list_enabled")))
                                cv.put("shop_list_order", c.getInt(c.getColumnIndex("id")))
                                db.insert("shop_lists_new", 0, cv)
                                c.moveToNext()
                            }
                        }
                        db.execSQL("DROP TABLE IF EXISTS `shop_lists`")
                        db.execSQL("ALTER TABLE shop_lists_new RENAME TO shop_lists")
                    }
                } catch (e: SQLiteException) {
                    throw Exception(e)
                } catch (e: Exception) {
                    throw Exception(e)
                }
            }
        }


        fun createNewShopListsTable(database: SupportSQLiteDatabase) {
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS `shop_lists_new` (
                            `id` INTEGER NOT NULL,
                            `shop_list_name` TEXT NOT NULL,
                            `shop_list_enabled` INTEGER NOT NULL,
                            `shop_list_order` INTEGER NOT NULL,
                            PRIMARY KEY(`id`))""".trimIndent()
            )
        }

        fun getInstance(context: Context): AppDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .addMigrations(migration11to14)
                .build()
        }
    }
}