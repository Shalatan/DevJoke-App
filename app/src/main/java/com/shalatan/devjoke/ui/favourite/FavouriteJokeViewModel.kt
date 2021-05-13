package com.shalatan.devjoke.ui.favourite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shalatan.devjoke.database.JokeDAO
import com.shalatan.devjoke.database.SavedJoke
import kotlinx.coroutines.launch

class FavouriteJokeViewModel(private val db: JokeDAO) : ViewModel() {

    val favouriteJokes: LiveData<List<SavedJoke>> = db.getAllSavedJokes()

    init {
        Log.e("OverviewViewModel : ", " view model created")
    }

    fun deleteSavedJoke(position: Int) {
        val savedJoke = favouriteJokes.value?.get(position)
        viewModelScope.launch {
            if (savedJoke != null) {
                db.delete(savedJoke)
            }
        }
    }
}