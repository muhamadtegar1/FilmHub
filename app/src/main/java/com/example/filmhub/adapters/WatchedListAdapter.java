package com.example.filmhub.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.filmhub.R;
import com.example.filmhub.database.entities.WatchedMovie;
import java.util.ArrayList;
import java.util.List;

public class WatchedListAdapter extends RecyclerView.Adapter<WatchedListAdapter.WatchedViewHolder> {

    private List<WatchedMovie> watchedMovieList = new ArrayList<>();

    @NonNull
    @Override
    public WatchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watched_movie, parent, false);
        return new WatchedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchedViewHolder holder, int position) {
        WatchedMovie watchedMovie = watchedMovieList.get(position);
        holder.bind(watchedMovie);
    }

    @Override
    public int getItemCount() {
        return watchedMovieList.size();
    }

    public void setWatchedMovieList(List<WatchedMovie> watchedMovieList) {
        this.watchedMovieList = watchedMovieList;
        notifyDataSetChanged();
    }

    class WatchedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvReviewSnippet;
        RatingBar rbRating;

        public WatchedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_watched_poster);
            tvTitle = itemView.findViewById(R.id.tv_watched_title);
            tvReviewSnippet = itemView.findViewById(R.id.tv_watched_review_snippet);
            rbRating = itemView.findViewById(R.id.rb_watched_rating);
        }

        public void bind(WatchedMovie movie) {
            tvTitle.setText(movie.title);
            tvReviewSnippet.setText(movie.userReview);
            rbRating.setRating(movie.userRating);

            String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.posterPath;
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image_placeholder)
                    .into(ivPoster);

            // Nanti bisa ditambahkan OnClickListener di sini jika diperlukan
        }
    }
}
