package com.shalatan.devjoke.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_jokes_table")
data class SavedJoke(
    @PrimaryKey
    val jokeId: Int,
    val jokeText: String
)
