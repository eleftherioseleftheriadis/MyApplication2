package com.example.myapplication2
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication2.R
import com.example.myapplication2.Movie
import com.example.myapplication2.TrendingMoviesResponse


class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val onMovieClick: (Movie) -> Unit // Add this if you want to handle clicks
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieImageView: ImageView = view.findViewById(R.id.movieImageView)
        val titleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
        val overviewTextView: TextView = view.findViewById(R.id.movieOverviewTextView)
        val genreTextView: TextView = view.findViewById(R.id.movieGenreTextView)
        val likeButton: Button = view.findViewById(R.id.likeButton)
        val watchlistButton: Button = view.findViewById(R.id.addToWatchlistButton)

        fun bind(movie: Movie) {
            // Check if the poster path is not null and construct the full image URL; otherwise, use a default image resource
            val imageUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            Glide.with(itemView.context)
                .load(imageUrl ?: R.drawable.default_placeholder) // Use the image URL or a default placeholder if URL is null
                .placeholder(R.drawable.default_placeholder) // Placeholder image while loading
                .error(R.drawable.error_placeholder) // Error placeholder in case of failure
                .into(movieImageView)

            titleTextView.text = movie.title
            overviewTextView.text = movie.overview

            // Assuming you convert genre IDs to genre names elsewhere
            // If not, ensure to handle conversion here or use a placeholder
            genreTextView.text = movie.genreIds?.joinToString(", ") { it.toString() } ?: "No genres available"

            likeButton.setOnClickListener {
                // Implement like action
            }
            watchlistButton.setOnClickListener {
                // Implement add to watchlist action
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener { onMovieClick(movie) } // Use the lambda function
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}
