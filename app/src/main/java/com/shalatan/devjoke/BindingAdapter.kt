package com.shalatan.devjoke

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.ui.overview.JokeAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,data: List<Joke>){
    val adapter = recyclerView.adapter as JokeAdapter
    adapter.submitList(data)
}