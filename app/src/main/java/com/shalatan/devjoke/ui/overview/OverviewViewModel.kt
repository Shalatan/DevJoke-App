package com.shalatan.devjoke.ui.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.remote.JokesDatabase
import kotlinx.coroutines.launch

class OverviewViewModel(val application: Application) : ViewModel() {

    private val _jokes = MutableLiveData<List<Joke>>()
    val jokes : LiveData<List<Joke>>
        get() = _jokes

    init {
        Log.e("OverviewViewModel : "," view model created")
        viewModelScope.launch {
            _jokes.value = JokesDatabase.getAllJokes()
        }
    }

    fun buttonClicked(joke : String){
        Log.e("Clicked",joke)
    }

}