package com.shalatan.devjoke.ui.submitJoke

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shalatan.devjoke.R
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.databinding.FragmentSubmitJokeBinding

class SubmitJokeFragment : Fragment() {

    private lateinit var binding: FragmentSubmitJokeBinding
    private var isConditionAccepted = false
    private var isJokePostingActive = true
    private var jokeId = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        binding = FragmentSubmitJokeBinding.inflate(inflater)
        val db = Firebase.firestore

        binding.postJokeButton.setOnClickListener { view ->
            if (isJokePostingActive) {
                val jokeText = binding.postJokeEditText.text.toString()
                val submittedJoke = Joke(jokeId,jokeText)
                db.collection("jokes").document(jokeId.toString()).set(submittedJoke)
                    .addOnSuccessListener {
                        Log.d("JOKE UPLOADING : ", "SUCCESSFUL")
                        Snackbar.make(
                            view,
                            "Thanks For Your Contribution !!",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(resources.getColor(R.color.dark_green))
                            .show()
                        jokeId++
                    }.addOnFailureListener {
                        Log.d("JOKE UPLOADING : ", "FAILED")
                        Snackbar.make(
                            view,
                            "Seems Like Something Unexpected Happened",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(resources.getColor(R.color.dark_green))
                            .show()
                    }
                binding.triggerMotionSceneButton.performClick()
                binding.postJokeEditText.text = null
                binding.postJokeEditText.hideKeyboard()
                makeButtonPostAgain(binding.postJokeButton)
            } else {
                binding.triggerMotionSceneButton.performClick()
                makeButtonPostJoke(binding.postJokeButton)
            }
        }

        binding.postJokeEditText.doOnTextChanged { newJoke, start, before, count ->
            binding.exampleCardTextView.text = newJoke
        }

        val cardView = binding.exampleCardView

        binding.acceptButton.setOnClickListener {
            if (!isConditionAccepted) {
                flipCardView(cardView)
            }
            binding.exampleCardTextView.textSize = 32f
        }
        return binding.root
    }

    /**
     * flip card view
     */
    private fun flipCardView(cardView: MaterialCardView) {
        isConditionAccepted = true
        val oa1 = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                cardView.elevation = 8f
                binding.acceptButton.isEnabled = false
                binding.postJokeButton.isEnabled = true
                binding.postJokeEditText.isEnabled = true
                binding.exampleCardTextView.text = null
                oa2.start()
            }
        })
        oa1.start()
    }

    /**
     * hide soft-keyboard
     */
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun makeButtonPostJoke(postJokeButton: Button) {
        postJokeButton.text = "Post Joke"
        binding.postJokeEditText.isEnabled = true
        isJokePostingActive = true
    }

    private fun makeButtonPostAgain(postJokeButton: Button) {
        postJokeButton.text = "Post New Joke"
        binding.postJokeEditText.isEnabled = false
        isJokePostingActive = false
    }

}