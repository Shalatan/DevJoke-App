package com.shalatan.devjoke.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JokeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savedJoke: SavedJoke)

    @Query("SELECT * FROM saved_jokes_table")
    fun getAllSavedJokes(): LiveData<List<SavedJoke>>

}