package com.shalatan.devjoke.ui.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.shalatan.devjoke.R
import com.shalatan.devjoke.databinding.FragmentOverviewBinding
import com.shalatan.devjoke.util.Constants
import com.shalatan.devjoke.util.ZoomOutPageTransformer
import com.shalatan.devjoke.util.shareView
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "OverviewFragment : "
const val VIEW_PAGER_POSITION = "com.shalatan.devjoke.VIEW_PAGER_POSITION"

@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private val viewModel : OverviewViewModel by viewModels()

    private lateinit var jokesViewPager: ViewPager2
    private lateinit var binding: FragmentOverviewBinding
    private var isRedActive = false
    private var viewPagerPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOverviewBinding.inflate(inflater)

        jokesViewPager = binding.jokesViewer
        val jokeAdapter = JokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        jokesViewPager.setPageTransformer(ZoomOutPageTransformer())

        //observe jokesData and submit it to viewPager adapter
        viewModel.jokesData.observe(viewLifecycleOwner) {
            it.let(jokeAdapter::submitList)
            scroll()
            Log.e(TAG, "Jokes Fetched")
        }

        //when scrolled, check if new joke is already liked by the user
        jokesViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewPagerPosition = position
                viewModel.isJokeSavedInDatabase(position)
                makeButtonLikeable()
            }
        })

        //observe isJokeExistInDb to check if current joke is already liked or not
        viewModel.isJokeExistInDb.observe(viewLifecycleOwner) {
            if (it) {
                makeButtonDisLikeable()
            } else {
                makeButtonLikeable()
            }
        }

        //save the current joke and make necessary changes to button
        binding.likeButton.setOnClickListener {
            val jokePosition = jokesViewPager.currentItem
            if (isRedActive) {
                viewModel.deleteJoke(jokePosition)
                Snackbar.make(it, "Joke Removed From Favourites", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.dark_green))
                    .show()
                Log.d("OverviewFragment : ", "deleted joke")
                makeButtonLikeable()
            } else {
                viewModel.saveJoke(jokePosition)
                Snackbar.make(it, "Joke Added To Favourites", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.dark_green))
                    .show()
                Log.d("OverviewFragment : ", "inserted joke")
                makeButtonDisLikeable()
            }
        }

        //share carView as image
        binding.shareButton.setOnClickListener {
            shareCardView(binding.jokesViewer.children.single())
        }

        binding.savedListButton.setOnClickListener {
//            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
//            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false)
            findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToFavouriteJokeFragment())
        }

        binding.addJokeButton.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.jokesViewer to "card_transition")
            val directions =
                OverviewFragmentDirections.actionOverviewFragmentToSubmitJokeFragment()
            findNavController().navigate(directions, extras)
        }
        return binding.root
    }

    //call this function wherever necessary
    private fun scroll() {
        val sharedPreferences =
            activity?.getSharedPreferences(VIEW_PAGER_POSITION, Context.MODE_PRIVATE) ?: return
        viewPagerPosition = sharedPreferences.getInt(VIEW_PAGER_POSITION, 0)
        Log.d(TAG + "Enter Position - ", viewPagerPosition.toString())
        jokesViewPager.setCurrentItem(viewPagerPosition, false)
    }

    //save the current position of viewPager
    override fun onPause() {
        super.onPause()
        val sharedPreferences =
            activity?.getSharedPreferences(VIEW_PAGER_POSITION, Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putInt(VIEW_PAGER_POSITION, viewPagerPosition)
            apply()
        }
        Log.d(TAG + "Exit Position - ", sharedPreferences.getInt(VIEW_PAGER_POSITION, 0).toString())
    }

    /**
     * set the drawable to red heart when user likes the current joke or current joke was already liked
     */
    private fun makeButtonDisLikeable() {
        binding.likeButton.setImageResource(R.drawable.ic_red_heart)
        isRedActive = true
    }

    /**
     * set the drawable to white heart when user unlikes the joke or current joke was not already liked
     */
    private fun makeButtonLikeable() {
        binding.likeButton.setImageResource(R.drawable.ic_heart)
        isRedActive = false
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