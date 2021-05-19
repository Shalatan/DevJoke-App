package com.shalatan.devjoke.ui.submitJoke

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.databinding.FragmentSubmitJokeBinding


class SubmitJokeFragment : Fragment() {

    private lateinit var binding: FragmentSubmitJokeBinding
    private var jokeNumber = 1001
    private var isConditionAccepted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSubmitJokeBinding.inflate(inflater)
        val db = Firebase.firestore
        binding.postJokeButton.setOnClickListener {
            jokeNumber++
            val jokes = binding.postJokeEditText.text.toString()
            val joke = Joke(jokeNumber, jokes)
            db.collection("jokes").document(jokeNumber.toString()).set(joke).addOnSuccessListener {
                Log.e("JOKE", "SUCCESSFUL")
            }.addOnFailureListener {
                Log.e("JOKE", "FAILED")
            }
            binding.postJokeEditText.text = null
        }

        binding.postJokeEditText.doOnTextChanged { newJoke, start, before, count ->
            binding.exampleCardTextView.text = newJoke
        }

        val cardView = binding.exampleCardView
        cardView.setOnClickListener {
            if (!isConditionAccepted) {
                isConditionAccepted = true
                val oa1 = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f)
                val oa2 = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f)
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.postJokeButton.isEnabled = true
                        binding.postJokeEditText.isEnabled = true
                        binding.exampleCardTextView.text = null
                        binding.exampleCardTextView.textSize = 30F
                        oa2.start()
                    }
                })
                oa1.start()
            }
        }
        return binding.root
    }

}