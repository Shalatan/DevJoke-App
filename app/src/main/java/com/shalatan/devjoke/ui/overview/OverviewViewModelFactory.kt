package com.shalatan.devjoke.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shalatan.devjoke.database.JokeRepository

class OverviewViewModelFactory(
    private val datasource: JokeRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}