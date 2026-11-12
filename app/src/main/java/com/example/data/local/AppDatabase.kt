package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CropDao
import com.example.data.local.dao.FarmDao
import com.example.data.local.dao.ReminderDao
import com.example.data.local.dao.ScanDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.CropEntity
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.FieldEntity
import com.example.data.local.entity.MonitoringLogEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.ScanEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FarmEntity::class,
        CropEntity::class,
        FieldEntity::class,
        ScanEntity::class,
        MonitoringLogEntity::class,
        ReminderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun farmDao(): FarmDao
    abstract fun cropDao(): CropDao
    abstract fun scanDao(): ScanDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "krishi_ai_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
