package com.example.filmhub.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // Untuk mengambil drawable
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.filmhub.R;
import com.example.filmhub.data.model.Genre;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.viewmodel.DetailViewModel;
import com.example.filmhub.fragments.ReviewInputDialogFragment; // Akan di-import nanti

import java.util.stream.Collectors;

public class DetailActivity extends AppCompatActivity {

    // Kunci untuk menerima data dari Intent
    public static final String EXTRA_MOVIE_ID = "movieId";

    // Deklarasi Komponen UI
    private ImageView ivPoster, ivBackdrop;
    private TextView tvTitle, tvRating, tvReleaseDate, tvDuration, tvGenres, tvOverview;
    private Button btnFavorite, btnWatched;
    private ProgressBar progressBar;

    // Deklarasi komponen logika
    private DetailViewModel detailViewModel;
    private int movieId;
    private boolean isFavorite = false; // State untuk tombol favorit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (!getIntentData()) {
            return; // Keluar jika tidak ada movie ID
        }

        initViews();
        initViewModel();
        observeViewModel();
        setupListeners();

        // Memuat semua data untuk movie ID ini
        progressBar.setVisibility(View.VISIBLE);
        detailViewModel.loadAllData(movieId);
    }

    // Mengambil movieId dari Intent
    private boolean getIntentData() {
        if (getIntent().hasExtra(EXTRA_MOVIE_ID)) {
            movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
            if (movieId != -1) {
                return true;
            }
        }
        // Jika tidak ada ID, tampilkan pesan error dan tutup activity
        Toast.makeText(this, "Film tidak ditemukan.", Toast.LENGTH_SHORT).show();
        finish();
        return false;
    }

    // Inisialisasi semua view dari layout XML
    private void initViews() {
        ivPoster = findViewById(R.id.iv_detail_poster);
        // ivBackdrop = findViewById(R.id.iv_detail_backdrop); // Jika ada
        tvTitle = findViewById(R.id.tv_detail_title);
        tvRating = findViewById(R.id.tv_detail_rating);
        tvReleaseDate = findViewById(R.id.tv_detail_release_date);
        tvDuration = findViewById(R.id.tv_detail_duration);
        tvGenres = findViewById(R.id.tv_detail_genres);
        tvOverview = findViewById(R.id.tv_detail_overview);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnWatched = findViewById(R.id.btn_watched);
        progressBar = findViewById(R.id.progress_bar_detail);
    }

    // Inisialisasi ViewModel
    private void initViewModel() {
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
    }

    // Mengobservasi perubahan data dari ViewModel
    private void observeViewModel() {
        // Observasi detail film dari API
        detailViewModel.getMovieDetailsLiveData().observe(this, movieDetails -> {
            progressBar.setVisibility(View.GONE);
            if (movieDetails != null) {
                populateUi(movieDetails);
            } else {
                Toast.makeText(this, "Gagal memuat detail film.", Toast.LENGTH_SHORT).show();
            }
        });

        // Observasi status favorit dari database
        detailViewModel.getFavoriteStatusLiveData().observe(this, favoriteMovie -> {
            isFavorite = (favoriteMovie != null);
            updateFavoriteButtonState(isFavorite);
        });

        // Observasi status "sudah ditonton" dari database
        detailViewModel.getWatchedStatusLiveData().observe(this, watchedMovie -> {
            if (watchedMovie != null) {
                btnWatched.setText("Lihat/Edit Catatan Tontonan");
                // Anda bisa menambahkan logika lain, misalnya mengubah warna tombol
            } else {
                btnWatched.setText("Tandai Sudah Ditonton");
            }
        });
    }

    // Mengisi data dari objek MovieDetailResponse ke komponen UI
    private void populateUi(MovieDetailResponse movie) {
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText("Rilis: " + movie.getReleaseDate());
        tvRating.setText(String.format("⭐ %.1f", movie.getVoteAverage()));
        tvDuration.setText(movie.getRuntime() + " menit");

        // Menggabungkan nama genre menjadi satu string
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String genres = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            tvGenres.setText(genres);

        }

        // Memuat gambar poster menggunakan Glide
        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(this)
                .load(posterUrl)
                .into(ivPoster);
    }

    // Mengatur listener untuk tombol-tombol
    private void setupListeners() {
        btnFavorite.setOnClickListener(v -> {
            detailViewModel.toggleFavorite();
        });

        btnWatched.setOnClickListener(v -> {
            // Logika untuk menampilkan dialog input review akan ada di sini
            showReviewDialog();
        });
    }

    // Mengubah tampilan tombol favorit berdasarkan status
    private void updateFavoriteButtonState(boolean isFavorite) {
        if (isFavorite) {
            btnFavorite.setText("Hapus dari Favorit");
            // Mengubah ikon drawable di sebelah kiri tombol
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_favorite_filled), null, null, null);
        } else {
            btnFavorite.setText("Tambahkan ke Favorit");
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border), null, null, null);
        }
    }

    // Metode untuk menampilkan dialog input review
    private void showReviewDialog() {
        // Membuat instance baru dari dialog kita
        ReviewInputDialogFragment dialog = ReviewInputDialogFragment.newInstance(movieId);
        // Menampilkan dialog
        dialog.show(getSupportFragmentManager(), "ReviewDialog");
    }
}
