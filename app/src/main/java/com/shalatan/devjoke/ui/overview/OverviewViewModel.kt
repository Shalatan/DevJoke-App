package com.shalatan.devjoke.ui.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.database.JokeDAO
import com.shalatan.devjoke.database.SavedJoke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OverviewViewModel(val application: Application, val db: JokeDAO) : ViewModel() {

    private val _jokesData = MutableLiveData<List<Joke>>()
    val jokesData: LiveData<List<Joke>>
        get() = _jokesData

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.e("OverviewViewModel : ", " view model created")
        coroutineScope.launch {
            getJokes()
        }
    }

    private suspend fun getJokes() {
        if (_jokesData.value.isNullOrEmpty()) {
            _jokesData.value = FirebaseFirestore.getInstance().collection("jokes").get().await()
                .toObjects(Joke::class.java)
            Log.e("Jokes in View model : ", _jokesData.value.toString())
        }
    }

    fun saveJoke(position: Int) {
        val joke = _jokesData.value?.get(position)
        val savedJoke = SavedJoke(joke!!.jokeId, joke.jokeText, false, 0, 0)
        viewModelScope.launch {
            db.insert(savedJoke)
        }
    }
}