package com.shalatan.devjoke.ui.overview

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

class OverviewViewModel(private val db: JokeDAO) : ViewModel() {

    private val _jokesData = MutableLiveData<List<Joke>>()
    val jokesData: LiveData<List<Joke>>
        get() = _jokesData

    private val _isJokeExistInDb = MutableLiveData<Boolean>()
    val isJokeExistInDb: LiveData<Boolean>
        get() = _isJokeExistInDb

    val favouriteJokes: LiveData<List<SavedJoke>> = db.getAllSavedJokes()

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.e("OverviewViewModel : ", " view model created")
        coroutineScope.launch {
            getJokes()
        }
    }

    /**
     * fetch jokes from firestore
     */
    private suspend fun getJokes() {
        if (_jokesData.value.isNullOrEmpty()) {
            _jokesData.value = FirebaseFirestore.getInstance().collection("jokes").get().await()
                .toObjects(Joke::class.java)
            Log.e("OverviewViewModel : ", "Jokes Fetched")
        }
    }

    /**
     * get the current item's position of viewPager and save that joke in ROOM database
     */
    fun saveJoke(position: Int) {
        val joke = _jokesData.value?.get(position)
        val savedJoke = SavedJoke(joke!!.jokeId, joke.jokeText, false, 0, 0)
        coroutineScope.launch {
            db.insert(savedJoke)
        }
    }

    /**
     * get the current item's position of viewPager and remove that joke from ROOM database
     */
    fun deleteJoke(position: Int) {
        val joke = _jokesData.value?.get(position)
        val savedJoke = SavedJoke(joke!!.jokeId, joke.jokeText, false, 0, 0)
        viewModelScope.launch {
            db.delete(savedJoke)
        }
    }

    /**
     * check if joke at 'position' already exists or not
     */
    fun isJokeSavedInDatabase(position: Int) {
        val currentJokeId = _jokesData.value?.get(position)!!.jokeId
        coroutineScope.launch {
            val jokeSaved = db.isJokeSaved(currentJokeId)
            _isJokeExistInDb.value = jokeSaved != 0
            Log.e("OverviewViewModel : ", jokeSaved.toString())
        }
    }
}