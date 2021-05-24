package com.shalatan.devjoke.ui.favourite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentFavouriteJokeBinding
import com.shalatan.devjoke.util.ZoomOutPageTransformer
import com.shalatan.devjoke.util.shareView

const val shareText =
    "Install https://play.google.com/store/apps/details?id=com.shalatan.devjoke for more such DevJokes and share your DevJokes/Puns with other devs"

class FavouriteJokeFragment : Fragment() {

    private lateinit var viewModelFactory: FavouriteJokeViewModelFactory
    private lateinit var viewModel: FavouriteJokeViewModel
    private lateinit var binding: FragmentFavouriteJokeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavouriteJokeBinding.inflate(inflater)

//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        val dataSource = JokeDatabase.getInstance(requireContext()).jokeDAO

        viewModelFactory = FavouriteJokeViewModelFactory(dataSource)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(FavouriteJokeViewModel::class.java)

        //set up view pager
        val jokesViewPager = binding.jokesViewer
        val jokeAdapter = FavouriteJokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        jokesViewPager.setPageTransformer(ZoomOutPageTransformer())

        //observe jokesData and submit it to viewPager adapter
        viewModel.favouriteJokes.observe(viewLifecycleOwner, {
            it.let(jokeAdapter::submitList)
            if (it.isEmpty()) {
                binding.shareButton.isClickable = false
                binding.likeButton.isClickable = false
                binding.shareButton.isPressed = true
                binding.likeButton.isPressed = true
            } else {
                binding.likeButton.isClickable = true
                binding.shareButton.isClickable = true
            }
            Log.e("FavouriteJokeFragment Saved Jokes : ", it?.size.toString())
        })

        //share carView as image
        binding.shareButton.setOnClickListener {
            shareCardView(binding.jokesViewer.children.single())
        }

        binding.likeButton.setOnClickListener {
            val position = jokesViewPager.currentItem
            viewModel.deleteSavedJoke(position)
            Toast.makeText(requireContext(), "Joke Removed from Favourites", Toast.LENGTH_SHORT)
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, com.shalatan.devjoke.ui.favourite.shareText)
        shareIntent.type = "image/png"
        startActivity(shareIntent)
    }

}