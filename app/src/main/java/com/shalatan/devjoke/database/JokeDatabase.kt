package com.shalatan.devjoke.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedJoke::class], version = 1, exportSchema = false)
abstract class JokeDatabase : RoomDatabase() {
    abstract fun jokesDao(): JokeDAO
}