package com.shalatan.devjoke

import android.graphics.Color
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.ui.overview.JokeAdapter
import java.util.*

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Joke>) {
    val adapter = recyclerView.adapter as JokeAdapter
    adapter.submitList(data)
}

@BindingAdapter("cardViewColor")
fun changeCardViewColor(cardView: MaterialCardView, jokeId: Int) {
    val rnd = Random()
    val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    cardView.setStrokeColor(color)
}

