package com.shalatan.devjoke.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding: FragmentOverviewBinding
    private lateinit var arrayAdapter: ArrayAdapter<Joke>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(activity).application
        binding = FragmentOverviewBinding.inflate(inflater)
        val dataSource = JokeDatabase.getInstance(requireContext()).jokeDAO

        viewModelFactory = OverviewViewModelFactory(application,dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)

        val rv = binding.jokesViewer
        val jokeAdapter = JokeAdapter()
        rv.adapter = jokeAdapter
        setUpPosterViewPager(rv)

        viewModel.jokesData.observe(viewLifecycleOwner, Observer {
            Log.e("Jokes in Fragment", it.toString())
            it.let(jokeAdapter::submitList)
        })

        return binding.root
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