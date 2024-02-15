package com.example.myapplication2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoviesAdapter(private var movies: MutableList<Movie>) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
        val overviewTextView: TextView = view.findViewById(R.id.movieOverviewTextView)
        val genreTextView: TextView = view.findViewById(R.id.movieGenreTextView)
        val likeButton: Button = view.findViewById(R.id.likeButton)
        val watchlistButton: Button = view.findViewById(R.id.addToWatchlistButton)

        fun bind(movie: Movie) {
            titleTextView.text = movie.title
            overviewTextView.text = movie.overview
            genreTextView.text = movie.genreIds.joinToString(", ") { it.toString() } // Example placeholder for genre names

            // Implement like and watchlist button functionality here
            likeButton.setOnClickListener {
                // Handle like action
            }
            watchlistButton.setOnClickListener {
                // Handle add to watchlist action
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}
