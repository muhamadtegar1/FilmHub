package com.example.filmhub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmhub.R;
import com.example.filmhub.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter untuk menampilkan daftar film dalam sebuah RecyclerView.
 * Digunakan oleh HomeFragment dan FavoriteFragment.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final OnMovieItemClickListener listener;
    private Context context;

    /**
     * Interface untuk menangani event klik pada setiap item di RecyclerView.
     * Akan diimplementasikan oleh Fragment yang menggunakan adapter ini.
     */
    public interface OnMovieItemClickListener {
        void onMovieClick(int movieId);
    }

    // Constructor untuk adapter, menerima listener sebagai parameter.
    public MovieListAdapter(OnMovieItemClickListener listener) {
        this.listener = listener;
        this.movieList = new ArrayList<>(); // Inisialisasi list agar tidak null
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Mendapatkan context dari parent
        this.context = parent.getContext();
        // Membuat view baru dari layout item_movie.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Mendapatkan data film pada posisi tertentu
        Movie movie = movieList.get(position);
        // Mengikat data ke ViewHolder
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah total item dalam list
        return movieList != null ? movieList.size() : 0;
    }

    /**
     * Metode untuk mengupdate daftar film di adapter dan memberitahu RecyclerView untuk refresh.
     * Dipanggil dari Fragment setelah mendapatkan data baru dari ViewModel.
     * @param movieList Daftar film yang baru.
     */
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged(); // Cara sederhana. Untuk performa lebih baik, gunakan DiffUtil.
    }

    // REVISI: Tambahkan metode ini untuk pagination
    public void addMovies(List<Movie> newMovies) {
        int startPosition = movieList.size();
        movieList.addAll(newMovies);
        notifyItemRangeInserted(startPosition, newMovies.size());
    }

    /**
     * ViewHolder yang merepresentasikan satu item tampilan (satu film) di RecyclerView.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        // Deklarasi komponen UI di dalam item layout
        ImageView ivPoster;
        TextView tvTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dari layout item_movie.xml
            ivPoster = itemView.findViewById(R.id.iv_item_poster); // Ganti dengan ID Anda
            tvTitle = itemView.findViewById(R.id.tv_item_title);   // Ganti dengan ID Anda
        }

        /**
         * Metode untuk mengisi data film ke dalam komponen UI dan mengatur listener.
         * @param movie Objek film yang akan ditampilkan.
         * @param listener Listener untuk event klik.
         */
        public void bind(final Movie movie, final OnMovieItemClickListener listener) {
            tvTitle.setText(movie.getTitle());

            // ===================================================================================
            // REVISI: Tambahkan pengecekan null sebelum memuat gambar
            // ===================================================================================

            // 1. Ambil path posternya dulu
            String posterPath = movie.getPosterPath();

            // 2. Cek apakah path-nya ada dan tidak kosong
            if (posterPath != null && !posterPath.isEmpty()) {
                // KONDISI SUKSES: Jika ada, bangun URL dan muat dengan Glide
                String imageUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder) // Gambar saat loading
                        .error(R.drawable.ic_broken_image_placeholder)   // Gambar jika URL error
                        .into(ivPoster);
            } else {
                // KONDISI GAGAL: Jika tidak ada path, tampilkan gambar default
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_broken_image_placeholder) // Atau placeholder lain
                        .into(ivPoster);
            }

            // Mengatur OnClickListener pada seluruh item view (tidak berubah)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie.getId());
                }
            });
        }
    }
}

