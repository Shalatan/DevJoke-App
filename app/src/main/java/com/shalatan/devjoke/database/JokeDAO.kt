package com.shalatan.devjoke.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface JokeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savedJoke: SavedJoke)

    @Query("SELECT * FROM saved_jokes_table")
    fun getAllSavedJokes(): LiveData<List<SavedJoke>>

    @Delete
    suspend fun delete(savedJoke: SavedJoke)

    @Query("SELECT count(*)!=0 FROM saved_jokes_table WHERE jokeId = :uid")
    suspend fun isJokeSaved(uid: Int): Int

}