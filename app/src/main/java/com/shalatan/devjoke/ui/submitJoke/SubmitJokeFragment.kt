package com.shalatan.devjoke.ui.submitJoke

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.databinding.FragmentOverviewBinding
import com.shalatan.devjoke.databinding.FragmentSubmitJokeBinding

class SubmitJokeFragment : Fragment() {

    private lateinit var binding: FragmentSubmitJokeBinding
    private var jokeNumber = 12


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSubmitJokeBinding.inflate(inflater)

        val db = Firebase.firestore

        binding.submitJoke.setOnClickListener{
            jokeNumber++
            val jokes = binding.postJokeEditText.text.toString()
            val joke = Joke(jokeNumber,jokes)
            db.collection("jokes").document(jokeNumber.toString()).set(joke).addOnSuccessListener {
                Log.e("JOKE","SUCCESSFUL")
            }.addOnFailureListener {
                Log.e("JOKE","FAILED")
            }
        }

        return binding.root
    }

}