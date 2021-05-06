package com.shalatan.devjoke.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.shalatan.devjoke.data.Joke
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object JokesDatabase {

    suspend fun getAllJokes(): List<Joke>{
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val jokesCollection = firestore.collection("jokes")
            jokesCollection.get().await().toObjects(Joke::class.java)
        } catch (e: Exception){
            emptyList()
        }
    }
}