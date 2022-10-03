package com.shalatan.devjoke.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shalatan.devjoke.database.JokeRepository
import com.shalatan.devjoke.database.SavedJoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteJokeViewModel @Inject constructor(
    private val repository: JokeRepository
) : ViewModel() {

    val favouriteJokes: LiveData<List<SavedJoke>> = repository.getAllJokes

    private val firestoreDB = FirebaseFirestore.getInstance().collection("jokes")

    fun deleteSavedJoke(position: Int) {
        val savedJoke = favouriteJokes.value?.get(position)
        viewModelScope.launch {
            if (savedJoke != null) {
                repository.deleteJoke(savedJoke)
                firestoreDB.document(savedJoke.jokeId.toString()).update("jokeLiked", FieldValue.increment(-1))
            }
        }
    }
}