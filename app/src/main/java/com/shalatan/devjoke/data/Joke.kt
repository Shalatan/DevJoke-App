package com.shalatan.devjoke.data

data class Joke(
    val jokeId: Int = 0,
    val jokeText: String = "",
    val jokeSent: Boolean = false,
    val jokeLiked: Int = 0,
    val jokeShared: Int = 0
)

