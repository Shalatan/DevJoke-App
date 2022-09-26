package com.shalatan.devjoke.database

import javax.inject.Inject

class JokeRepository @Inject constructor(private val dao: JokeDAO) {

    val getAllJokes = dao.getAllSavedJokes()

    suspend fun insertJoke(savedJoke: SavedJoke) {
        dao.insert(savedJoke)
    }

    suspend fun deleteJoke(savedJoke: SavedJoke) {
        dao.delete(savedJoke)
    }

    suspend fun isJokeSaved(jokeId: Int): Int {
        return dao.isJokeSaved(jokeId)
    }
}