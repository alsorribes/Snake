package com.example.snake.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PartidaEntity::class], version = 1, exportSchema = false)
abstract class SnakeDatabase : RoomDatabase() {

    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var INSTANCE: SnakeDatabase? = null

        fun getInstance(context: Context): SnakeDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SnakeDatabase::class.java,
                    "snake_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}