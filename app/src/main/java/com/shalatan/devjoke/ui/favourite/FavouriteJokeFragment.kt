package com.shalatan.devjoke.ui.favourite

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.shalatan.devjoke.R
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentFavouriteJokeBinding
import com.shalatan.devjoke.databinding.FragmentOverviewBinding
import com.shalatan.devjoke.ui.overview.JokeAdapter
import com.shalatan.devjoke.ui.overview.OverviewViewModel
import com.shalatan.devjoke.ui.overview.OverviewViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.*


class FavouriteJokeFragment : Fragment() {

    private val SHARE_TEXT =
        "Install https://play.google.com/store/apps/details?id=com.shalatan.devjoke for more such DevJokes and share your DevJokes/Puns with other devs"
    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding: FragmentFavouriteJokeBinding
    private var isRedActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavouriteJokeBinding.inflate(inflater)
        val dataSource = JokeDatabase.getInstance(requireContext()).jokeDAO

        viewModelFactory = OverviewViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)

        //set up view pager
        val jokesViewPager = binding.jokesViewer
        val jokeAdapter = FavouriteJokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        setUpPosterViewPager(jokesViewPager)

        //observe jokesData and submit it to viewPager adapter
        viewModel.favouriteJokes.observe(viewLifecycleOwner, Observer {
            it.let(jokeAdapter::submitList)
            Log.e("OverviewFragment : ", "Jokes Fetched")
        })

        //share carView as image
        binding.shareButton.setOnClickListener {
            shareCardView(binding.jokesViewer.children.single())
        }

        binding.likeButton.setOnClickListener {
            val position = jokesViewPager.currentItem
            viewModel.deleteSavedJoke(position)
        }

        binding.savedListButton.setOnClickListener {
            findNavController().navigate(FavouriteJokeFragmentDirections.actionFavouriteJokeFragmentToOverviewFragment())
        }

        return binding.root
    }

    /**
     * function to get bitmap from viewPager and share ias image
     */
    private fun shareCardView(view: View) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)

        val uri = getImageUri(requireContext(), bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }

    /**
     * function to convert the passed bitmap to Uri and return it
     */
    private fun getImageUri(inContext: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, bitmap, "IMG_" + Calendar.getInstance().time, null
        )
        return Uri.parse(path)
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