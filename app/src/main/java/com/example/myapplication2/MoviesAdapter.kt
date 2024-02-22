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
import android.util.Log


class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val onMovieClick: (Movie) -> Unit,
    private val onLikeClick: (Movie) -> Unit = {}
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val movieImageView: ImageView = view.findViewById(R.id.movieImageView)
        private val titleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
        private val overviewTextView: TextView = view.findViewById(R.id.movieOverviewTextView)
        private val genreTextView: TextView = view.findViewById(R.id.movieGenreTextView)
        private val likeButton: Button = view.findViewById(R.id.likeButton)
        private val watchlistButton: Button = view.findViewById(R.id.addToWatchlistButton)

        fun bind(movie: Movie) {
            val imageUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/original/${movie.posterPath}" }
            Log.d("ImageURL", "Received URL: $imageUrl")
            Glide.with(itemView.context)
                .load(imageUrl ?: R.drawable.default_placeholder) // Use the image URL or a default placeholder
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.error_placeholder)
                .into(movieImageView)

            titleTextView.text = movie.title
            overviewTextView.text = movie.overview
            genreTextView.text = movie.genreIds?.joinToString(", ") { it.toString() } ?: "No genres available"

            itemView.setOnClickListener {
                onLikeClick(movie)
            }

            likeButton.setOnClickListener {
                movie.isLiked = !movie.isLiked
                onLikeClick(movie)
                notifyItemChanged(adapterPosition)
                }
            watchlistButton.setOnClickListener {
                onMovieClick(movie)
                notifyItemChanged(adapterPosition)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            onLikeClick(movie)
            onMovieClick(movie)
            movie.isLiked = !movie.isLiked
            movie.isWatched = !movie.isWatched
            notifyItemChanged(position)
        } // Use the lambda function
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        Log.d("MoviesAdapter", "Updating movies, new list size: ${newMovies.size}")
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}
