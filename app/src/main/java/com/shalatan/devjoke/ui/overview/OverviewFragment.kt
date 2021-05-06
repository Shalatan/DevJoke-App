package com.shalatan.devjoke.ui.overview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding: FragmentOverviewBinding

//    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOverviewBinding.inflate(inflater)

        viewModelFactory = OverviewViewModelFactory(requireNotNull(activity).application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(OverviewViewModel::class.java)

        viewModel.jokes.observe(viewLifecycleOwner, {
            Log.e("Done",it.toString())
        })

        return binding.root
    }

}