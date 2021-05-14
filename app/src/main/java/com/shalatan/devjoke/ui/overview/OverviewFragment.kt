package com.shalatan.devjoke.ui.overview

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.shalatan.devjoke.R
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentOverviewBinding
import com.shalatan.devjoke.transformers.ZoomOutPageTransformer
import java.io.ByteArrayOutputStream
import java.util.*


class OverviewFragment : Fragment() {

    private val shareText =
        "Install https://play.google.com/store/apps/details?id=com.shalatan.devjoke for more such DevJokes and share your DevJokes/Puns with other devs"
    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var viewModel: OverviewViewModel
    private lateinit var binding: FragmentOverviewBinding
    private var isRedActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOverviewBinding.inflate(inflater)
        val dataSource = JokeDatabase.getInstance(requireContext()).jokeDAO

        viewModelFactory = OverviewViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)

        //set up view pager
        val jokesViewPager = binding.jokesViewer
        val jokeAdapter = JokeAdapter()
        jokesViewPager.adapter = jokeAdapter
        jokesViewPager.setPageTransformer(ZoomOutPageTransformer())
//        setUpPosterViewPager(jokesViewPager)

        //observe jokesData and submit it to viewPager adapter
        viewModel.jokesData.observe(viewLifecycleOwner, {
            it.let(jokeAdapter::submitList)
            Log.e("OverviewFragment : ", "Jokes Fetched")
        })

        //when scrolled, check if new joke is already liked by the user
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
        viewModel.isJokeExistInDb.observe(viewLifecycleOwner, {
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
                Toast.makeText(requireContext(), "Joke Removed from Favourites", Toast.LENGTH_SHORT)
                    .show()
                Log.e("OverviewFragment : ", "deleted joke")
                makeButtonLikeable()
            } else {
                viewModel.saveJoke(jokePosition)
                Toast.makeText(requireContext(), "Joke Added to Favourites", Toast.LENGTH_SHORT)
                    .show()
                Log.e("OverviewFragment : ", "inserted joke")
                makeButtonDisLikeable()
            }
        }

        //share carView as image
        binding.shareButton.setOnClickListener {
            shareCardView(binding.jokesViewer.children.single())
        }

        binding.savedListButton.setOnClickListener {
            findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToFavouriteJokeFragment())
        }

        binding.addJokeButton.setOnClickListener {
            findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToSubmitJokeFragment())
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
        intent.putExtra(Intent.EXTRA_TEXT,shareText)
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


//    /**
//     * function to make view pager view multiple items
//     */
//    private fun setUpPosterViewPager(jokeViewPager: ViewPager2) {
//        with(jokeViewPager) {
//            clipToPadding = false
//            clipChildren = false
//            offscreenPageLimit = 3
//        }
//        val pageMarginPx = 20 * resources.displayMetrics.density
//        val offsetPx = 15 * resources.displayMetrics.density
//        jokeViewPager.setPageTransformer { page, position ->
//            val viewPager = page.parent.parent as ViewPager2
//            val offset = position * -(2 * offsetPx + pageMarginPx)
//            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
//                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
//                    page.translationX = -offset
//                } else {
//                    page.translationX = offset
//                }
//            } else {
//                page.translationY = offset
//            }
//        }
//    }
}