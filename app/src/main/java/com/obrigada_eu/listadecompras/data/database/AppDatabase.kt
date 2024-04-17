package com.obrigada_eu.listadecompras.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.core.database.getDoubleOrNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ShopItemDbModel::class,
        ShopListDbModel::class
    ],
    version = 11,
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
//                .addMigrations(migration2to11, migration11to14, migration14to15)
                .addMigrations(migration2to11)
                .build()
        }



        private val migration2to11 = object : Migration(2, 11) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    val c1 = db.query("SELECT * FROM shop_items")
                    c1.use {
                        createNewShopItemsTable2To11(db)

                        if (c1.moveToFirst()) {
                            val cv = ContentValues()
                            while (!c1.isAfterLast) {
                                cv.clear()
                                cv.put(
                                    "shop_item_id",
                                    c1.getInt(c1.getColumnIndex("id"))
                                )
                                cv.put("name", c1.getString(c1.getColumnIndex("name")))
                                cv.put("count", c1.getDouble(c1.getColumnIndex("count")))
                                cv.put("enabled", c1.getInt(c1.getColumnIndex("enabled")))
                                cv.put(
                                    "shop_item_order",
                                    c1.getInt(c1.getColumnIndex("shop_item_order"))
                                )
                                cv.put("shop_list_id", 1)

                                db.insert("shop_items_new", 0, cv)
                                c1.moveToNext()
                            }
                        }
                        db.execSQL("DROP TABLE IF EXISTS `shop_items`")
                        db.execSQL("ALTER TABLE shop_items_new RENAME TO shop_items")
                    }


                    val c2 = db.query("SELECT * FROM shop_items")
                    c2.use {
                        createNewShopListsTable2To11(db)


                        if (c2.moveToFirst()) {

                            val enabled = true
                            val listName = "My First List"
                            val cv = ContentValues()
                                cv.clear()
                                cv.put("shop_list_name", listName)
                                cv.put("shop_list_enabled", enabled)
                                db.insert("shop_lists_new", 0, cv)
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






            private val migration11to14 = object : Migration(11, 14) {
                @SuppressLint("Range")
                override fun migrate(db: SupportSQLiteDatabase) {
                    try {
                        val c = db.query("SELECT * FROM shop_lists")
                        c.use {
                            createNewShopListsTable(db)
                            if (c.moveToFirst()) {
                                var position = -1
                                val cv = ContentValues()
                                while (!c.isAfterLast) {
                                    position++
                                    cv.clear()
                                    cv.put("id", c.getInt(c.getColumnIndex("id")))
                                    cv.put(
                                        "shop_list_name",
                                        c.getString(c.getColumnIndex("shop_list_name"))
                                    )
                                    cv.put(
                                        "shop_list_enabled",
                                        c.getInt(c.getColumnIndex("shop_list_enabled"))
                                    )
                                    cv.put("shop_list_order", position)
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

            private val migration14to15 = object : Migration(14, 15) {
                @SuppressLint("Range")
                override fun migrate(db: SupportSQLiteDatabase) {
                    try {
                        val c = db.query("SELECT * FROM shop_items")
                        c.use {
                            createNewShopItemsTable(db)
                            if (c.moveToFirst()) {
                                val cv = ContentValues()
                                while (!c.isAfterLast) {
                                    cv.clear()
                                    cv.put(
                                        "shop_item_id",
                                        c.getInt(c.getColumnIndex("shop_item_id"))
                                    )
                                    cv.put("name", c.getString(c.getColumnIndex("name")))
                                    cv.put("count", c.getDoubleOrNull(c.getColumnIndex("count")))
                                    cv.put("enabled", c.getInt(c.getColumnIndex("enabled")))
                                    cv.put(
                                        "shop_item_order",
                                        c.getInt(c.getColumnIndex("shop_item_order"))
                                    )
                                    cv.put(
                                        "shop_list_id",
                                        c.getInt(c.getColumnIndex("shop_list_id"))
                                    )
                                    db.insert("shop_items_new", 0, cv)
                                    c.moveToNext()
                                }
                            }
                            db.execSQL("DROP TABLE IF EXISTS `shop_items`")
                            db.execSQL("ALTER TABLE shop_items_new RENAME TO shop_items")
                        }
                    } catch (e: SQLiteException) {
                        throw Exception(e)
                    } catch (e: Exception) {
                        throw Exception(e)
                    }
                }
            }

            private fun createNewShopItemsTable(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `shop_items_new` (
                            `shop_item_id` INTEGER NOT NULL,
                            `name` TEXT NOT NULL,
                            `count` REAL,
                            `units` TEXT,
                            `enabled` INTEGER NOT NULL,
                            `shop_item_order` INTEGER NOT NULL,
                            `shop_list_id` INTEGER NOT NULL,
                            PRIMARY KEY(`shop_item_id`),
                            FOREIGN KEY(`shop_list_id`) REFERENCES `shop_lists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)""".trimIndent()
                )
            }

            private fun createNewShopItemsTable2To11(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `shop_items_new` (
                            `shop_item_id` INTEGER NOT NULL,
                            `name` TEXT NOT NULL,
                            `count` REAL NOT NULL,
                            `enabled` INTEGER NOT NULL,
                            `shop_item_order` INTEGER NOT NULL,
                            `shop_list_id` INTEGER NOT NULL,
                            PRIMARY KEY(`shop_item_id`),
                            FOREIGN KEY(`shop_list_id`) REFERENCES `shop_lists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)""".trimIndent()
                )
            }


            private fun createNewShopListsTable(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `shop_lists_new` (
                            `id` INTEGER NOT NULL,
                            `shop_list_name` TEXT NOT NULL,
                            `shop_list_enabled` INTEGER NOT NULL,
                            `shop_list_order` INTEGER NOT NULL,
                            PRIMARY KEY(`id`))""".trimIndent()
                )
            }


            private fun createNewShopListsTable2To11(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `shop_lists_new` (
                            `id` INTEGER NOT NULL,
                            `shop_list_name` TEXT NOT NULL,
                            `shop_list_enabled` INTEGER NOT NULL,
                            PRIMARY KEY(`id`))""".trimIndent()
                )
            }

        }
    }
}