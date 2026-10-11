package com.example.makerhub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.makerhub.data.local.dao.BorrowerDao
import com.example.makerhub.data.local.dao.CheckoutDao
import com.example.makerhub.data.local.dao.ItemDao
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity

@Database(
    entities = [
        ItemEntity::class,
        BorrowerEntity::class,
        CheckoutEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun borrowerDao(): BorrowerDao
    abstract fun checkoutDao(): CheckoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "makerhub_db.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

