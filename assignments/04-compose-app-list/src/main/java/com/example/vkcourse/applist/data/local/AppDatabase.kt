package com.example.vkcourse.applist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson

class ScreenshotConverters {
    @TypeConverter
    fun toJson(urls: List<String>): String = Gson().toJson(urls)

    @TypeConverter
    fun fromJson(json: String): List<String> = Gson().fromJson(json, Array<String>::class.java).toList()
}

// Это первая локальная схема проекта: до задания 11 Room не использовался.
@Database(entities = [AppDetailsEntity::class], version = 1, exportSchema = true)
@TypeConverters(ScreenshotConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDetailsDao(): AppDetailsDao
}
