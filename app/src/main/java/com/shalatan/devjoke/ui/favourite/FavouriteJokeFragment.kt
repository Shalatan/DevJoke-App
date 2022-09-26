package com.shalatan.devjoke.ui.favourite

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shalatan.devjoke.R
import com.shalatan.devjoke.databinding.FragmentFavouriteJokeBinding
import com.shalatan.devjoke.util.Constants
import com.shalatan.devjoke.util.ZoomOutPageTransformer
import com.shalatan.devjoke.util.shareView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteJokeFragment : Fragment() {

    private val viewModel: FavouriteJokeViewModel by viewModels()
    private lateinit var binding: FragmentFavouriteJokeBinding
    private lateinit var favouriteAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavouriteJokeBinding.inflate(inflater)

//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
//        val constraintLayout = binding.favouriteJokeFragmentConstraintLayout.apply {
//            setBackgroundResource(R.drawable.favourite_fragment_animation)
//            favouriteAnimation = background as AnimationDrawable
//        }
//        favouriteAnimation.start()

        //set up view pager
        val jokesViewPager = binding.jokesViewer
        val jokeAdapter = FavouriteJokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        jokesViewPager.setPageTransformer(ZoomOutPageTransformer())

        //observe jokesData and submit it to viewPager adapter
        viewModel.favouriteJokes.observe(viewLifecycleOwner) {
            it.let(jokeAdapter::submitList)
            if (it.isEmpty()) {
                binding.jokesViewer.visibility = View.GONE
                binding.emptyImage.visibility = View.VISIBLE
                binding.shareButton.isClickable = false
                binding.likeButton.isClickable = false
                binding.shareButton.isPressed = true
                binding.likeButton.isPressed = true
            } else {
                binding.jokesViewer.visibility = View.VISIBLE
                binding.emptyImage.visibility = View.GONE
                binding.likeButton.isClickable = true
                binding.shareButton.isClickable = true
            }
            Log.e("FavouriteJokeFragment Saved Jokes : ", it?.size.toString())
        }

        //share cardView as image
        binding.shareButton.setOnClickListener {
            shareCardView(binding.jokesViewer.children.single())
        }

        binding.likeButton.setOnClickListener {
            val position = jokesViewPager.currentItem
            viewModel.deleteSavedJoke(position)
            Snackbar.make(it, "Joke Removed From Favourites", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.dark_green))
                .show()
        }

        binding.savedListButton.setOnClickListener {
            findNavController().navigate(FavouriteJokeFragmentDirections.actionFavouriteJokeFragmentToOverviewFragment())
        }

        return binding.root
    }

    /**
     * function to create a bitmap of the view and save it in internal storage
     */
    private fun shareCardView(view: View) {
        val uri = shareView(requireContext(), view)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.INTENT_MESSAGE)
        shareIntent.type = "image/png"
        startActivity(shareIntent)
    }

}