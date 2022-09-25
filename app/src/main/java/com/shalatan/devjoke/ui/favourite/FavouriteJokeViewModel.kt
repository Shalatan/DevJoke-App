package com.shalatan.devjoke.ui.favourite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shalatan.devjoke.database.JokeRepository
import com.shalatan.devjoke.database.SavedJoke
import kotlinx.coroutines.launch

class FavouriteJokeViewModel(private val repository: JokeRepository) : ViewModel() {

    val favouriteJokes: LiveData<List<SavedJoke>> = repository.getAllJokes

    private val firestoreDB = FirebaseFirestore.getInstance().collection("jokes")

    init {
        Log.e("OverviewViewModel : ", " view model created")
    }

    fun deleteSavedJoke(position: Int) {
        val savedJoke = favouriteJokes.value?.get(position)
        viewModelScope.launch {
            if (savedJoke != null) {
                repository.deleteJoke(savedJoke)
                decrementJokeLikedCount(position)
            }
        }
    }

    private fun decrementJokeLikedCount(position: Int) {
        val jokeId = favouriteJokes.value?.get(position)!!.jokeId
        firestoreDB.document(jokeId.toString()).update("jokeLiked", FieldValue.increment(-1))
    }
}