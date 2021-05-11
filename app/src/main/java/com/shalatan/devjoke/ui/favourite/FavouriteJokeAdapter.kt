package com.shalatan.devjoke.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.database.SavedJoke
import com.shalatan.devjoke.databinding.ItemFavourtieJokeBinding
import com.shalatan.devjoke.databinding.ItemJokeBinding

class FavouriteJokeAdapter :
    ListAdapter<SavedJoke, FavouriteJokeAdapter.JokeViewHolder>(DiffCallBack) {

    class JokeViewHolder(private val binding: ItemFavourtieJokeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(savedJoke: SavedJoke) {
            binding.savedJoke = savedJoke
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<SavedJoke>() {
        override fun areItemsTheSame(oldItem: SavedJoke, newItem: SavedJoke): Boolean {
            return newItem === oldItem
        }

        override fun areContentsTheSame(oldItem: SavedJoke, newItem: SavedJoke): Boolean {
            return oldItem.jokeId == newItem.jokeId
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JokeViewHolder {
        return JokeViewHolder(
            ItemFavourtieJokeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        val joke = getItem(position)
        holder.bind(joke)
    }
}