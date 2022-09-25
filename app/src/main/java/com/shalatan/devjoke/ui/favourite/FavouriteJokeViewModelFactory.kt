package com.shalatan.devjoke.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shalatan.devjoke.database.JokeRepository

class FavouriteJokeViewModelFactory(
    private val dataSource: JokeRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteJokeViewModel::class.java)) {
            return FavouriteJokeViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}