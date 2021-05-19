package com.shalatan.devjoke.ui.favourite

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shalatan.devjoke.database.JokeDatabase
import com.shalatan.devjoke.databinding.FragmentFavouriteJokeBinding
import com.shalatan.devjoke.util.ZoomOutPageTransformer
import java.io.ByteArrayOutputStream
import java.util.*


class FavouriteJokeFragment : Fragment() {

    private val shareText =
        "Install https://play.google.com/store/apps/details?id=com.shalatan.devjoke for more such DevJokes and share your DevJokes/Puns with other devs"
    private lateinit var viewModelFactory: FavouriteJokeViewModelFactory
    private lateinit var viewModel: FavouriteJokeViewModel
    private lateinit var binding: FragmentFavouriteJokeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavouriteJokeBinding.inflate(inflater)
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
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
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

}