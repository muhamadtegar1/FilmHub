package com.example.filmhub.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieResponse;
import com.example.filmhub.data.repository.MovieRepository;

/**
 * ViewModel untuk HomeFragment.
 * Bertanggung jawab untuk menyiapkan dan mengelola data untuk UI.
 * Berkomunikasi dengan MovieRepository untuk mengambil data.
 */
public class HomeViewModel extends AndroidViewModel { // <-- PENTING: Extend AndroidViewModel, bukan ViewModel

    private final MovieRepository movieRepository;
    private LiveData<MovieResponse> movieListLiveData;
    private LiveData<GenreResponse> genresLiveData;

    // Constructor yang menerima Application. Ini dimungkinkan karena kita extend AndroidViewModel.
    public HomeViewModel(@NonNull Application application) {
        super(application);
        // Inisialisasi repository di sini.
        // Sekarang 'movieRepository' sudah dikenali.
        movieRepository = MovieRepository.getInstance(application);
    }

    // ===================================================================================
    // METODE UNTUK MENDAPATKAN LIVE DATA (DIOBSERVASI OLEH FRAGMENT)
    // ===================================================================================

    /**
     * LiveData yang berisi daftar film untuk ditampilkan di RecyclerView.
     */
    public LiveData<MovieResponse> getMovieListLiveData() {
        return movieListLiveData;
    }

    /**
     * LiveData yang berisi daftar genre untuk ditampilkan sebagai Chip.
     */
    public LiveData<GenreResponse> getGenresLiveData() {
        return genresLiveData;
    }

    // ===================================================================================
    // METODE UNTUK MEMICU PENGAMBILAN DATA DARI REPOSITORY (DIPANGGIL DARI FRAGMENT)
    // ===================================================================================

    /**
     * Mengambil daftar genre dari repository.
     */
    public void fetchGenres() {
        genresLiveData = movieRepository.getGenres();
    }

    /**
     * Memuat daftar film default (misalnya, yang paling populer).
     * Dipanggil saat pertama kali fragment dimuat atau saat pencarian dibatalkan.
     */
    public void loadDefaultMovies() {
        // "popularity.desc" untuk paling populer, "" untuk tanpa filter genre, 1 untuk halaman pertama.
        movieListLiveData = movieRepository.getDiscoverMovies("popularity.desc", "", 1);
    }

    /**
     * Memuat daftar film berdasarkan query pencarian.
     */
    public void searchMovies(String query, int page) {
        movieListLiveData = movieRepository.searchMovies(query, page);
    }

    /**
     * Memuat daftar film berdasarkan filter genre dan sortir yang sedang aktif.
     * (Contoh sederhana, bisa dikembangkan lebih lanjut)
     */
    public void loadMoviesWithFilters(String sortBy, String genreIds) {
        movieListLiveData = movieRepository.getDiscoverMovies(sortBy, genreIds, 1);
    }
}