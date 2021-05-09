package com.shalatan.devjoke.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.shalatan.devjoke.R
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding: FragmentOverviewBinding
    private var isRedActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(activity).application
        binding = FragmentOverviewBinding.inflate(inflater)
        val dataSource = JokeDatabase.getInstance(requireContext()).jokeDAO

        viewModelFactory = OverviewViewModelFactory(application, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)

        //set up view pager
        val jokesViewPager = binding.jokesViewer
        val jokeAdapter = JokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        setUpPosterViewPager(jokesViewPager)

        //observe jokesData and submit it to viewPager adapter
        viewModel.jokesData.observe(viewLifecycleOwner, Observer {
            Log.e("Jokes in Fragment", it.toString())
            it.let(jokeAdapter::submitList)
        })

        //when scrolled, check if new joke is or not already liked by the user
        jokesViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewModel.isJokeSavedInDatabase(position)
                makeButtonLikeable()
            }
        })

        //observe isJokeExistInDb to check if current joke is already liked or not
        //it = true when joke is already liked
        viewModel.isJokeExistInDb.observe(viewLifecycleOwner, Observer {
            Log.e("OverviewFragment Joke Exist", it.toString())
            if (it) {
                makeButtonDisLikeable()
            } else {
                makeButtonLikeable()
            }
        })

        //save the current joke and make necessary changes to button
        binding.likeButton.setOnClickListener {
            val jokePosition = jokesViewPager.currentItem
            if (isRedActive) {
                viewModel.deleteJoke(jokePosition)
                Log.e("OverviewFragment : ", "deleted joke")
                makeButtonLikeable()
            } else {
                viewModel.saveJoke(jokePosition)
                Log.e("OverviewFragment : ", "inserted joke")
                makeButtonDisLikeable()
            }
        }

        return binding.root
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
     * function to make view pager view multiple items
     */
    private fun setUpPosterViewPager(jokeViewPager: ViewPager2) {
        with(jokeViewPager) {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
        }
        val pageMarginPx = 20 * resources.displayMetrics.density
        val offsetPx = 15 * resources.displayMetrics.density
        jokeViewPager.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
    }
}