package com.shalatan.devjoke.ui.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.database.JokeDAO
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.database.SavedJoke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OverviewViewModel(application: Application) : ViewModel() {

    private val db: JokeDAO = JokeDatabase.getInstance(application).jokeDAO

    private val _jokesData = MutableLiveData<List<Joke>>()
    val jokesData: LiveData<List<Joke>>
        get() = _jokesData

    private val _isJokeExistInDb = MutableLiveData<Boolean>()
    val isJokeExistInDb: LiveData<Boolean>
        get() = _isJokeExistInDb

//    var likeCounter = MutableLiveData<Int?>()

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val firestoreDB = FirebaseFirestore.getInstance().collection("jokes")

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
            _jokesData.value = firestoreDB.get().await()
                .toObjects(Joke::class.java)
            Log.e("OverviewViewModel : ", "Jokes Fetched")
        }
    }

    /**
     * get the current item's position of viewPager and save that joke in ROOM database
     */
    fun saveJoke(position: Int) {
        val joke = _jokesData.value?.get(position)
        val savedJoke = SavedJoke(joke!!.jokeId, joke.jokeText)
        coroutineScope.launch {
            db.insert(savedJoke)
            incrementJokeLikedCount(position)
        }
//        likeCounter.value = joke.jokeLiked
    }

    private fun incrementJokeLikedCount(position: Int) {
        val jokeId = _jokesData.value?.get(position)!!.jokeId
        firestoreDB.document(jokeId.toString()).update("jokeLiked", FieldValue.increment(1))
    }

    /**
     * get the current item's position of viewPager and remove that joke from ROOM database
     */
    fun deleteJoke(position: Int) {
        val joke = _jokesData.value?.get(position)
        val savedJoke = SavedJoke(joke!!.jokeId, joke.jokeText)
        viewModelScope.launch {
            db.delete(savedJoke)
            decrementJokeLikedCount(position)
        }
    }

    private fun decrementJokeLikedCount(position: Int) {
        val jokeId = _jokesData.value?.get(position)!!.jokeId
        firestoreDB.document(jokeId.toString()).update("jokeLiked", FieldValue.increment(-1))
    }

    /**
     * check if joke at 'position' already exists or not
     */
    fun isJokeSavedInDatabase(position: Int) {
        val currentJokeId = _jokesData.value?.get(position)!!.jokeId
        coroutineScope.launch {
            val jokeSaved = db.isJokeSaved(currentJokeId)
            _isJokeExistInDb.value = jokeSaved != 0
        }
    }
}